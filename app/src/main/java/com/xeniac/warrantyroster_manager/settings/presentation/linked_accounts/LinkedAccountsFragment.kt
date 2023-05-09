package com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.core.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentLinkedAccountsBinding
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_ALREADY_LINKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_GOOGLE_SIGN_IN_API_NOT_AVAILABLE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showActionSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showIntentAppNotFoundError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LinkedAccountsFragment @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    var viewModel: LinkedAccountsViewModel? = null
) : Fragment(R.layout.fragment_linked_accounts) {

    private var _binding: FragmentLinkedAccountsBinding? = null
    val binding get() = _binding!!

    private lateinit var currentAppLanguage: String

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding?.let {
            it.isGoogleConnected = false
            it.isTwitterConnected = false
            it.isFacebookConnected = false
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLinkedAccountsBinding.bind(view)
        viewModel = viewModel
            ?: ViewModelProvider(requireActivity())[LinkedAccountsViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        toolbarNavigationBackOnClick()
        subscribeToObservers()
        getCurrentAppLanguage()
        getLinkedAccounts()
        googleOnClick()
        twitterOnClick()
        facebookOnClick()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigateBack()
        }
    }

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        } else {
            networkStatus = ConnectivityObserver.Status.AVAILABLE
        }
    }

    private fun toolbarNavigationBackOnClick() = binding.toolbar.setNavigationOnClickListener {
        navigateBack()
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun subscribeToObservers() {
        currentAppLanguageObserver()
        linkedAccountsObserver()
        linkGoogleAccountObserver()
        unlinkGoogleAccountObserver()
        linkTwitterAccountObserver()
        unlinkTwitterAccountObserver()
        linkFacebookAccountObserver()
        unlinkFacebookAccountObserver()
    }

    private fun getCurrentAppLanguage() = viewModel!!.getCurrentAppLanguage()

    private fun currentAppLanguageObserver() =
        viewModel!!.currentAppLanguageLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { language ->
                            currentAppLanguage = language
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
            }
        }

    private fun getLinkedAccounts() = viewModel!!.getLinkedAccounts()

    private fun linkedAccountsObserver() =
        viewModel!!.linkedAccountsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showAllLoadingAnimations()
                    is Resource.Success -> {
                        hideAllLoadingAnimations()
                        response.data?.let { showLinkedAccountsStatus(it) }
                    }
                    is Resource.Error -> {
                        hideAllLoadingAnimations()
                        binding.apply {
                            googleClickable = false
                            twitterClickable = false
                            facebookClickable = false
                        }
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
            }
        }

    private fun showLinkedAccountsStatus(linkedAccounts: List<String>) = binding.apply {
        isGoogleConnected = linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)

        isTwitterConnected = linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_TWITTER)

        isFacebookConnected = linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)
    }

    private fun googleOnClick() = binding.apply {
        cvGoogle.setOnClickListener {
            if (isGoogleConnected!!) unlinkGoogleAccount() else linkGoogleAccount()
        }
    }

    private fun unlinkGoogleAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel!!.unlinkGoogleAccount()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { unlinkGoogleAccount() }
            Timber.e("unlinkGoogleAccount Error: Offline")
        }
    }

    private fun unlinkGoogleAccountObserver() =
        viewModel!!.unlinkGoogleAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showGoogleLoadingAnimation()
                    is Resource.Success -> {
                        hideGoogleLoadingAnimation()
                        binding.isGoogleConnected = false
                    }
                    is Resource.Error -> {
                        hideGoogleLoadingAnimation()
                        binding.isGoogleConnected = true
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> {
                                    showSomethingWentWrongError(requireContext(), requireView())
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun linkGoogleAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            launchGoogleSignInClient()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { linkGoogleAccount() }
            Timber.e("linkGoogleAccount Error: Offline")
        }
    }

    private fun launchGoogleSignInClient() = CoroutineScope(Dispatchers.Main).launch {
        try {
            showGoogleLoadingAnimation()
            val options = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).apply {
                requestIdToken(BuildConfig.GOOGLE_AUTH_SERVER_CLIENT_ID)
                requestId()
            }.build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), options)
            googleSignInClient.signOut().await()

            linkGoogleAccountResultLauncher.launch(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            hideGoogleLoadingAnimation()
            e.message?.let { message ->
                snackbar = if (message.contains(ERROR_GOOGLE_SIGN_IN_API_NOT_AVAILABLE)) {
                    showIntentAppNotFoundError(requireContext(), requireView())
                } else {
                    showSomethingWentWrongError(requireContext(), requireView())
                }
                Timber.e("launchGoogleSignInClient Exception: $message")
            }
        }
    }

    private val linkGoogleAccountResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.let { viewModel!!.linkGoogleAccount(account) }
        } catch (e: Exception) {
            hideGoogleLoadingAnimation()
            e.message?.let {
                when {
                    it.contains(ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED) -> {
                        /* NO-OP */
                    }
                    it.contains(ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE) -> {
                        snackbar = showUnavailableNetworkConnectionError(
                            requireContext(), requireView()
                        ) { linkGoogleAccount() }
                    }
                    else -> {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
                Timber.e("linkGoogleAccountResultLauncher Exception: $it")
            }
        }
    }

    private fun linkGoogleAccountObserver() =
        viewModel!!.linkGoogleAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showGoogleLoadingAnimation()
                    is Resource.Success -> {
                        hideGoogleLoadingAnimation()
                        binding.isGoogleConnected = true
                    }
                    is Resource.Error -> {
                        hideGoogleLoadingAnimation()
                        binding.isGoogleConnected = false
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> {
                                    showSomethingWentWrongError(requireContext(), requireView())
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun twitterOnClick() = binding.apply {
        cvTwitter.setOnClickListener {
            if (isTwitterConnected!!) unlinkTwitterAccount() else checkPendingLinkTwitterAccountAuthResult()
        }
    }

    private fun unlinkTwitterAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel!!.unlinkTwitterAccount()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { unlinkTwitterAccount() }
            Timber.e("unlinkTwitterAccount Error: Offline")
        }
    }

    private fun unlinkTwitterAccountObserver() =
        viewModel!!.unlinkTwitterAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showTwitterLoadingAnimation()
                    is Resource.Success -> {
                        hideTwitterLoadingAnimation()
                        binding.isTwitterConnected = false
                    }
                    is Resource.Error -> {
                        hideTwitterLoadingAnimation()
                        binding.isTwitterConnected = true
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> {
                                    showSomethingWentWrongError(requireContext(), requireView())
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun checkPendingLinkTwitterAccountAuthResult() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            showTwitterLoadingAnimation()

            val pendingAuthResult = firebaseAuth.pendingAuthResult

            if (pendingAuthResult != null) {
                pendingLinkTwitterAccountAuthResult(pendingAuthResult)
            } else {
                linkTwitterAccount()
            }
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { checkPendingLinkTwitterAccountAuthResult() }
            Timber.e("checkPendingLinkTwitterAccountAuthResult Error: Offline")
        }
    }

    private fun pendingLinkTwitterAccountAuthResult(pendingAuthResult: Task<AuthResult>) {
        pendingAuthResult.addOnCompleteListener { task ->
            handleLinkTwitterAccountAuthResult(task)
        }
    }

    private fun linkTwitterAccount() {
        val oAuthProvider = OAuthProvider.newBuilder(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
        oAuthProvider.addCustomParameter("lang", currentAppLanguage)

        firebaseAuth.currentUser!!.startActivityForLinkWithProvider(
            requireActivity(), oAuthProvider.build()
        ).addOnCompleteListener { task ->
            handleLinkTwitterAccountAuthResult(task)
        }
    }

    private fun handleLinkTwitterAccountAuthResult(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val authCredential = task.result.credential
            authCredential?.let { credential ->
                viewModel!!.linkTwitterAccount(credential)
            }
        } else {
            hideTwitterLoadingAnimation()
            task.exception?.message?.let { message ->
                Timber.e("handleLinkTwitterAccountAuthResult Exception: $message")
                if (message.contains(ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED)) {
                    /* NO-OP */
                } else {
                    snackbar = when {
                        message.contains(ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION) -> {
                            showNetworkFailureError(requireContext(), requireView())
                        }
                        message.contains(
                            ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
                        ) -> {
                            showActionSnackbarError(
                                requireView(),
                                requireContext().getString(R.string.linked_accounts_error_email_exists_with_different_credentials),
                                requireContext().getString(R.string.error_btn_confirm)
                            ) { snackbar?.dismiss() }
                        }
                        message.contains(ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS) -> {
                            showActionSnackbarError(
                                requireView(),
                                requireContext().getString(R.string.linked_accounts_error_email_exists),
                                requireContext().getString(R.string.error_btn_confirm)
                            ) { snackbar?.dismiss() }
                        }
                        else -> {
                            showSomethingWentWrongError(requireContext(), requireView())
                        }
                    }
                }
            }
        }
    }

    private fun linkTwitterAccountObserver() =
        viewModel!!.linkTwitterAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showTwitterLoadingAnimation()
                    is Resource.Success -> {
                        hideTwitterLoadingAnimation()
                        binding.isTwitterConnected = true
                    }
                    is Resource.Error -> {
                        hideTwitterLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            if (it.contains(ERROR_FIREBASE_AUTH_ALREADY_LINKED)) {
                                // Firebase Auth gives this exception, even though the account is linked successfully.
                                binding.isTwitterConnected = true
                            } else {
                                snackbar = when {
                                    it.contains(ERROR_NETWORK_CONNECTION) -> {
                                        showNetworkFailureError(requireContext(), requireView())
                                    }
                                    it.contains(ERROR_FIREBASE_403) -> {
                                        show403Error(requireContext(), requireView())
                                    }
                                    it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                        showFirebaseDeviceBlockedError(
                                            requireContext(), requireView()
                                        )
                                    }
                                    it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS) -> {
                                        showActionSnackbarError(
                                            requireView(),
                                            requireContext().getString(R.string.linked_accounts_error_email_exists),
                                            requireContext().getString(R.string.error_btn_confirm)
                                        ) { snackbar?.dismiss() }
                                    }
                                    else -> {
                                        showSomethingWentWrongError(requireContext(), requireView())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun facebookOnClick() = binding.apply {
        cvFacebook.setOnClickListener {
            if (isFacebookConnected!!) unlinkFacebookAccount() else linkFacebookAccount()
        }
    }

    private fun unlinkFacebookAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel!!.unlinkFacebookAccount()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { unlinkFacebookAccount() }
            Timber.e("unlinkFacebookAccount Error: Offline")
        }
    }

    private fun unlinkFacebookAccountObserver() =
        viewModel!!.unlinkFacebookAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showFacebookLoadingAnimation()
                    is Resource.Success -> {
                        hideFacebookLoadingAnimation()
                        binding.isFacebookConnected = false
                    }
                    is Resource.Error -> {
                        hideFacebookLoadingAnimation()
                        binding.isFacebookConnected = true
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> {
                                    showSomethingWentWrongError(requireContext(), requireView())
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun linkFacebookAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            launchFacebookLoginManager()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { linkFacebookAccount() }
            Timber.e("linkFacebookAccount Error: Offline")
        }
    }

    private fun launchFacebookLoginManager() {
        showFacebookLoadingAnimation()

        val callbackManager = CallbackManager.Factory.create()

        val loginManager = LoginManager.getInstance()
        loginManager.logInWithReadPermissions(this, callbackManager, listOf())

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel!!.linkFacebookAccount(result.accessToken)
            }

            override fun onCancel() {
                hideFacebookLoadingAnimation()
                Timber.i("launchFacebookLoginManager canceled.")
            }

            override fun onError(error: FacebookException) {
                hideFacebookLoadingAnimation()
                snackbar = showSomethingWentWrongError(requireContext(), requireView())
                Timber.e("launchFacebookLoginManager Callback Exception: ${error.message}")
            }
        })
    }

    private fun linkFacebookAccountObserver() =
        viewModel!!.linkFacebookAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showFacebookLoadingAnimation()
                    is Resource.Success -> {
                        hideFacebookLoadingAnimation()
                        binding.isFacebookConnected = true
                    }
                    is Resource.Error -> {
                        hideFacebookLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> {
                                    showSomethingWentWrongError(requireContext(), requireView())
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showAllLoadingAnimations() {
        showGoogleLoadingAnimation()
        showTwitterLoadingAnimation()
        showFacebookLoadingAnimation()
    }

    private fun hideAllLoadingAnimations() {
        hideGoogleLoadingAnimation()
        hideTwitterLoadingAnimation()
        hideFacebookLoadingAnimation()
    }

    private fun showGoogleLoadingAnimation() = binding.apply {
        googleClickable = false
        twitterClickable = false
        facebookClickable = false
        isGoogleLoading = true
    }

    private fun hideGoogleLoadingAnimation() = binding.apply {
        isGoogleLoading = false
        googleClickable = true
        twitterClickable = true
        facebookClickable = true
    }

    private fun showTwitterLoadingAnimation() = binding.apply {
        googleClickable = false
        twitterClickable = false
        facebookClickable = false
        isTwitterLoading = true
    }

    private fun hideTwitterLoadingAnimation() = binding.apply {
        isTwitterLoading = false
        googleClickable = true
        twitterClickable = true
        facebookClickable = true
    }

    private fun showFacebookLoadingAnimation() = binding.apply {
        googleClickable = false
        twitterClickable = false
        facebookClickable = false
        isFacebookLoading = true
    }

    private fun hideFacebookLoadingAnimation() = binding.apply {
        isFacebookLoading = false
        googleClickable = true
        twitterClickable = true
        facebookClickable = true
    }
}