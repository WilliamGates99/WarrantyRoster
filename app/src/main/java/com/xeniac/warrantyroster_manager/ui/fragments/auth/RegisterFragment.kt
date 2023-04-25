package com.xeniac.warrantyroster_manager.ui.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.xeniac.warrantyroster_manager.core.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.core.presentation.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.RegisterViewModel
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_GOOGLE_SIGN_IN_API_NOT_AVAILABLE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_REGISTER_CONFIRM_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_REGISTER_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_REGISTER_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.util.LinkHelper.openLink
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showActionSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showIntentAppNotFoundError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import com.xeniac.warrantyroster_manager.util.UserHelper.passwordStrength
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: RegisterViewModel

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var currentAppLanguage: String

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        subscribeToObservers()
        getCurrentAppLanguage()
        agreementOnclick()
        loginOnClick()
        registerOnClick()
        registerActionDone()
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
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)
            val password = binding.tiEditPassword.text.toString().trim()
            val confirmPassword = binding.tiEditConfirmPassword.text.toString().trim()

            if (email.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_REGISTER_EMAIL, email)
            }

            if (password.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_REGISTER_PASSWORD, password)
            }

            if (confirmPassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_REGISTER_CONFIRM_PASSWORD, confirmPassword)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_REGISTER_EMAIL)?.let { restoredEmail ->
                binding.tiEditEmail.setText(restoredEmail)
            }

            it.getString(SAVE_INSTANCE_REGISTER_PASSWORD)?.let { restoredPassword ->
                binding.tiEditPassword.setText(restoredPassword)
            }

            it.getString(SAVE_INSTANCE_REGISTER_CONFIRM_PASSWORD)?.let { restoredRetypePassword ->
                binding.tiEditConfirmPassword.setText(restoredRetypePassword)
            }
        }
        super.onViewStateRestored(savedInstanceState)
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

    private fun textInputsBackgroundColor() = binding.apply {
        tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
                tiLayoutPassword.isHelperTextEnabled = false
            }
        }

        tiEditConfirmPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutConfirmPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutConfirmPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditEmail.addTextChangedListener {
            tiLayoutEmail.isErrorEnabled = false
            tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        tiEditPassword.addTextChangedListener { inputPassword ->
            tiLayoutPassword.isErrorEnabled = false

            if (tiLayoutPassword.hasFocus()) {
                when (passwordStrength(inputPassword.toString())) {
                    (-1).toByte() -> {
                        tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.red)
                        tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_weak)
                        tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.red)
                        )
                    }
                    (0).toByte() -> {
                        tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_mediocre)
                        tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.orange)
                        )
                    }
                    (1).toByte() -> {
                        tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.green)
                        tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_strong)
                        tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.green)
                        )
                    }
                }
            }
        }

        tiEditConfirmPassword.addTextChangedListener {
            tiLayoutConfirmPassword.isErrorEnabled = false
            tiLayoutConfirmPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun subscribeToObservers() {
        currentAppLanguageObserver()
        registerWithEmailObserver()
        registerWithGoogleAccountObserver()
        registerWithTwitterAccountObserver()
        registerWithFacebookAccountObserver()
    }

    private fun getCurrentAppLanguage() = viewModel.getCurrentAppLanguage()

    private fun currentAppLanguageObserver() =
        viewModel.currentLanguageLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { language ->
                currentAppLanguage = language
            }
        }

    private fun agreementOnclick() = binding.btnAgreement.setOnClickListener {
        openLink(requireContext(), requireView(), URL_PRIVACY_POLICY)
    }

    private fun loginOnClick() = binding.btnLogin.setOnClickListener {
        popBackStack()
    }

    private fun registerOnClick() = binding.btnRegister.setOnClickListener {
        validateRegisterWithEmailInputs()
    }

    private fun registerActionDone() =
        binding.tiEditConfirmPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateRegisterWithEmailInputs()
            }
            false
        }

    private fun validateRegisterWithEmailInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)
            val password = binding.tiEditPassword.text.toString().trim()
            val retypePassword = binding.tiEditConfirmPassword.text.toString().trim()

            viewModel.validateRegisterWithEmailInputs(email, password, retypePassword)
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { validateRegisterWithEmailInputs() }
            Timber.e("validateRegisterWithEmailInputs Error: Offline")
        }
    }

    private fun registerWithEmailObserver() =
        viewModel.registerWithEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showRegisterWithEmailLoadingAnimation()
                    is Resource.Success -> {
                        hideRegisterWithEmailLoadingAnimation()
                        navigateToMainActivity()
                    }
                    is Resource.Error -> {
                        hideRegisterWithEmailLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.register_error_blank_email)
                                    binding.tiLayoutEmail.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_PASSWORD) -> {
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.register_error_blank_password)
                                    binding.tiLayoutPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_RETYPE_PASSWORD) -> {
                                    binding.tiLayoutConfirmPassword.error =
                                        requireContext().getString(R.string.register_error_blank_confirm_password)
                                    binding.tiLayoutConfirmPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.register_error_email)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_SHORT) -> {
                                    binding.tiLayoutPassword.requestFocus()
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.register_error_password_short)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_NOT_MATCH) -> {
                                    binding.tiLayoutConfirmPassword.requestFocus()
                                    binding.tiLayoutConfirmPassword.error =
                                        requireContext().getString(R.string.register_error_password_not_match)
                                }
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(), requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(), requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS) -> {
                                    snackbar = showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.register_error_account_exists),
                                        requireContext().getString(R.string.register_btn_login)
                                    ) { popBackStack() }
                                }
                                it.contains(
                                    ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
                                ) -> {
                                    showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.register_error_email_exists_with_different_credentials),
                                        requireContext().getString(R.string.error_btn_confirm)
                                    ) { snackbar?.dismiss() }
                                }
                                else -> {
                                    snackbar = showSomethingWentWrongError(
                                        requireContext(), requireView()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun navigateToMainActivity() = requireActivity().apply {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun googleOnClick() = binding.btnGoogle.setOnClickListener {
        registerWithGoogleAccount()
    }

    private fun registerWithGoogleAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            launchGoogleSignInClient()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { registerWithGoogleAccount() }
            Timber.e("registerWithGoogleAccount Error: Offline")
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
                requestEmail()
            }.build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), options)
            googleSignInClient.signOut().await()

            registerWithGoogleAccountResultLauncher.launch(googleSignInClient.signInIntent)
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

    private val registerWithGoogleAccountResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.let { viewModel.registerWithGoogleAccount(account) }
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
                        ) { registerWithGoogleAccount() }
                    }
                    else -> {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
                Timber.e("registerWithGoogleAccountResultLauncher Exception: $it")
            }
        }
    }

    private fun registerWithGoogleAccountObserver() =
        viewModel.registerWithGoogleAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showGoogleLoadingAnimation()
                    is Resource.Success -> {
                        hideGoogleLoadingAnimation()
                        navigateToMainActivity()
                    }
                    is Resource.Error -> {
                        hideGoogleLoadingAnimation()
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
                                it.contains(
                                    ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
                                ) -> {
                                    showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.register_error_email_exists_with_different_credentials),
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

    private fun twitterOnClick() = binding.btnTwitter.setOnClickListener {
        checkPendingLinkTwitterAccountAuthResult()
    }

    private fun checkPendingLinkTwitterAccountAuthResult() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            showTwitterLoadingAnimation()

            val pendingAuthResult = firebaseAuth.pendingAuthResult

            if (pendingAuthResult != null) {
                pendingLoginWithTwitterAccountAuthResult(pendingAuthResult)
            } else {
                registerWithTwitterAccount()
            }
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { checkPendingLinkTwitterAccountAuthResult() }
            Timber.e("checkPendingLinkTwitterAccountAuthResult Error: Offline")
        }
    }

    private fun pendingLoginWithTwitterAccountAuthResult(pendingAuthResult: Task<AuthResult>) {
        pendingAuthResult.addOnCompleteListener { task ->
            handleLoginWithTwitterAccountAuthResult(task)
        }
    }

    private fun registerWithTwitterAccount() {
        val oAuthProvider = OAuthProvider.newBuilder(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
        oAuthProvider.addCustomParameter("lang", currentAppLanguage)

        firebaseAuth.startActivityForSignInWithProvider(requireActivity(), oAuthProvider.build())
            .addOnCompleteListener { task ->
                handleLoginWithTwitterAccountAuthResult(task)
            }
    }

    private fun handleLoginWithTwitterAccountAuthResult(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val authCredential = task.result.credential
            authCredential?.let { credential ->
                viewModel.registerWithTwitterAccount(credential)
            }
        } else {
            hideTwitterLoadingAnimation()
            task.exception?.message?.let { message ->
                Timber.e("handleLoginWithTwitterAccountAuthResult Exception: $message")
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
                                requireContext().getString(R.string.register_error_email_exists_with_different_credentials),
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

    private fun registerWithTwitterAccountObserver() =
        viewModel.registerWithTwitterAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showTwitterLoadingAnimation()
                    is Resource.Success -> {
                        hideTwitterLoadingAnimation()
                        navigateToMainActivity()
                    }
                    is Resource.Error -> {
                        hideTwitterLoadingAnimation()
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
                                it.contains(
                                    ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
                                ) -> {
                                    showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.register_error_email_exists_with_different_credentials),
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

    private fun facebookOnClick() = binding.btnFacebook.setOnClickListener {
        registerWithFacebookAccount()
    }

    private fun registerWithFacebookAccount() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            launchFacebookLoginManager()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { registerWithFacebookAccount() }
            Timber.e("registerWithFacebookAccount Error: Offline")
        }
    }

    private fun launchFacebookLoginManager() {
        showFacebookLoadingAnimation()

        val callbackManager = CallbackManager.Factory.create()

        val loginManager = LoginManager.getInstance()
        loginManager.logInWithReadPermissions(this, callbackManager, listOf("email"))

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel.registerWithFacebookAccount(result.accessToken)
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

    private fun registerWithFacebookAccountObserver() =
        viewModel.registerWithFacebookAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showFacebookLoadingAnimation()
                    is Resource.Success -> {
                        hideFacebookLoadingAnimation()
                        navigateToMainActivity()
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
                                it.contains(
                                    ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS
                                ) -> {
                                    showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.register_error_email_exists_with_different_credentials),
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

    private fun showRegisterWithEmailLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        tiEditConfirmPassword.isEnabled = false
        btnRegister.isClickable = false
        btnGoogle.isClickable = false
        btnTwitter.isClickable = false
        btnFacebook.isClickable = false
        btnRegister.text = null
        cpiRegister.visibility = VISIBLE
    }

    private fun hideRegisterWithEmailLoadingAnimation() = binding.apply {
        cpiRegister.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        tiEditConfirmPassword.isEnabled = true
        btnRegister.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnRegister.text = requireContext().getString(R.string.register_btn_register)
    }

    private fun showGoogleLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        tiEditConfirmPassword.isEnabled = false
        btnRegister.isClickable = false
        btnGoogle.isClickable = false
        btnTwitter.isClickable = false
        btnFacebook.isClickable = false
        btnGoogle.icon = null
        cpiGoogle.visibility = VISIBLE
    }

    private fun hideGoogleLoadingAnimation() = binding.apply {
        cpiGoogle.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        tiEditConfirmPassword.isEnabled = true
        btnRegister.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnGoogle.setIconResource(R.drawable.ic_auth_google)
    }

    private fun showTwitterLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        tiEditConfirmPassword.isEnabled = false
        btnRegister.isClickable = false
        btnGoogle.isClickable = false
        btnTwitter.isClickable = false
        btnFacebook.isClickable = false
        btnTwitter.icon = null
        cpiTwitter.visibility = VISIBLE
    }

    private fun hideTwitterLoadingAnimation() = binding.apply {
        cpiTwitter.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        tiEditConfirmPassword.isEnabled = true
        btnRegister.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnTwitter.setIconResource(R.drawable.ic_auth_twitter)
    }

    private fun showFacebookLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        tiEditConfirmPassword.isEnabled = false
        btnRegister.isClickable = false
        btnGoogle.isClickable = false
        btnTwitter.isClickable = false
        btnFacebook.isClickable = false
        btnFacebook.icon = null
        cpiFacebook.visibility = VISIBLE
    }

    private fun hideFacebookLoadingAnimation() = binding.apply {
        cpiFacebook.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        tiEditConfirmPassword.isEnabled = true
        btnRegister.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnFacebook.setIconResource(R.drawable.ic_auth_facebook)
    }
}