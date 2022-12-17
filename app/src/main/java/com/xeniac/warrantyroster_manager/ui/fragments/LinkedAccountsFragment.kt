package com.xeniac.warrantyroster_manager.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.xeniac.warrantyroster_manager.databinding.FragmentLinkedAccountsBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.LinkedAccountsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ALREADY_LINKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showActionSnackbarError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showSomethingWentWrongError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LinkedAccountsFragment : Fragment(R.layout.fragment_linked_accounts) {

    private var _binding: FragmentLinkedAccountsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: LinkedAccountsViewModel

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var currentAppLanguage: String

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
        viewModel = ViewModelProvider(requireActivity())[LinkedAccountsViewModel::class.java]

        toolbarNavigationBackOnClick()
        subscribeToObservers()
        getCurrentAppLanguage()
        getLinkedAccounts()
        googleOnClick()
        twitterOnClick()
        facebookOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
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

    private fun getCurrentAppLanguage() = viewModel.getCurrentAppLanguage()

    private fun currentAppLanguageObserver() =
        viewModel.currentLanguageLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { language ->
                currentAppLanguage = language
            }
        }

    private fun getLinkedAccounts() = viewModel.getLinkedAccounts()

    private fun linkedAccountsObserver() =
        viewModel.linkedAccountsLiveData.observe(viewLifecycleOwner) { responseEvent ->
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

                        response.message?.asString(requireContext())?.let {
                            snackbar = showActionSnackbarError(
                                view = requireView(),
                                message = requireContext().getString(R.string.error_something_went_wrong),
                                actionBtn = requireContext().getString(R.string.error_btn_retry)
                            ) { getLinkedAccounts() }
                        }
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

    private fun unlinkGoogleAccount() = viewModel.unlinkGoogleAccount()

    private fun unlinkGoogleAccountObserver() =
        viewModel.unlinkGoogleAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { unlinkGoogleAccount() }
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
        CoroutineScope(Dispatchers.Main).launch {
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
                e.message?.let {
                    val isSignInClientCanceled = it.contains(ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED)
                    if (!isSignInClientCanceled) {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                    Timber.e("linkGoogleAccount Exception: $it")
                }
            }
        }
    }

    private val linkGoogleAccountResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.let { viewModel.linkGoogleAccount(account) }
        } catch (e: Exception) {
            hideGoogleLoadingAnimation()
            e.message?.let {
                snackbar = when {
                    it.contains(ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE) -> {
                        showNetworkConnectionError(
                            requireContext(), requireView()
                        ) { linkGoogleAccount() }
                    }
                    else -> {
                        showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
                Timber.e("linkGoogleAccountResultLauncher Exception: $it")
            }
        }
    }

    private fun linkGoogleAccountObserver() =
        viewModel.linkGoogleAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { unlinkGoogleAccount() }
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

    private fun unlinkTwitterAccount() = viewModel.unlinkTwitterAccount()

    private fun unlinkTwitterAccountObserver() =
        viewModel.unlinkTwitterAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { unlinkGoogleAccount() }
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
        showTwitterLoadingAnimation()

        val pendingAuthResult = firebaseAuth.pendingAuthResult

        if (pendingAuthResult != null) {
            pendingLinkTwitterAccountAuthResult(pendingAuthResult)
        } else {
            linkTwitterAccount()
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
                viewModel.linkTwitterAccount(credential)
            }
        } else {
            hideTwitterLoadingAnimation()
            task.exception?.message?.let { message ->
                if (message.contains(ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED)) {
                    /* NO-OP */
                } else {
                    snackbar = when {
                        message.contains(ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION) -> {
                            showNetworkConnectionError(requireContext(), requireView()) {
                                checkPendingLinkTwitterAccountAuthResult()
                            }
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
        viewModel.linkTwitterAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                        showNetworkConnectionError(
                                            requireContext(), requireView()
                                        ) { unlinkGoogleAccount() }
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

    private fun unlinkFacebookAccount() = viewModel.unlinkFacebookAccount()

    private fun unlinkFacebookAccountObserver() =
        viewModel.unlinkFacebookAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { unlinkGoogleAccount() }
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
        showFacebookLoadingAnimation()

        val callbackManager = CallbackManager.Factory.create()

        val loginManager = LoginManager.getInstance()
        loginManager.logInWithReadPermissions(this, callbackManager, listOf())

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel.linkFacebookAccount(result.accessToken)
            }

            override fun onCancel() {
                hideFacebookLoadingAnimation()
                Timber.i("linkFacebookAccount canceled.")
            }

            override fun onError(error: FacebookException) {
                hideFacebookLoadingAnimation()
                snackbar = showSomethingWentWrongError(requireContext(), requireView())
                Timber.e("linkFacebookAccount Callback Exception: ${error.message}")
            }
        })
    }

    private fun linkFacebookAccountObserver() =
        viewModel.linkFacebookAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { unlinkGoogleAccount() }
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