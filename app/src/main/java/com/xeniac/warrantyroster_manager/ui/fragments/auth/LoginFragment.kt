package com.xeniac.warrantyroster_manager.ui.fragments.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
import com.xeniac.warrantyroster_manager.ui.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.LoginViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_CREDENTIALS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_LOGIN_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_LOGIN_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showActionSnackbarError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNormalSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var currentAppLanguage: String

    var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        textInputsBackgroundColor()
        textInputsStrokeColor()
        subscribeToObservers()
        getCurrentAppLanguage()
        forgotPwOnClick()
        registerOnClick()
        loginOnClick()
        loginActionDone()
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
            requireActivity().finishAffinity()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)
            val password = binding.tiEditPassword.text.toString().trim()

            if (email.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_LOGIN_EMAIL, email)
            }

            if (password.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_LOGIN_PASSWORD, password)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_LOGIN_EMAIL)?.let { restoredEmail ->
                binding.tiEditEmail.setText(restoredEmail)
            }

            it.getString(SAVE_INSTANCE_LOGIN_PASSWORD)?.let { restoredPassword ->
                binding.tiEditPassword.setText(restoredPassword)
            }
        }
        super.onViewStateRestored(savedInstanceState)
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
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditEmail.addTextChangedListener {
            tiLayoutEmail.isErrorEnabled = false
            tiLayoutEmail.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }

        tiEditPassword.addTextChangedListener {
            tiLayoutPassword.isErrorEnabled = false
            tiLayoutPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun subscribeToObservers() {
        currentAppLanguageObserver()
        loginObserver()
        loginWithGoogleAccountObserver()
        loginWithTwitterAccountObserver()
    }

    private fun getCurrentAppLanguage() = viewModel.getCurrentAppLanguage()

    private fun currentAppLanguageObserver() =
        viewModel.currentLanguageLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { language ->
                currentAppLanguage = language
            }
        }

    private fun forgotPwOnClick() = binding.btnForgotPw.setOnClickListener {
        navigateToForgotPw()
    }

    private fun navigateToForgotPw() = findNavController()
        .navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())

    private fun registerOnClick() = binding.btnRegister.setOnClickListener {
        navigateToRegister()
    }

    private fun navigateToRegister() = findNavController()
        .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

    private fun loginOnClick() = binding.btnLogin.setOnClickListener {
        validateLoginInputs()
    }

    private fun loginActionDone() =
        binding.tiEditPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateLoginInputs()
            }
            false
        }

    private fun validateLoginInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)
        val password = binding.tiEditPassword.text.toString().trim()

        viewModel.validateLoginInputs(email, password)
    }

    private fun loginObserver() =
        viewModel.loginLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoginLoadingAnimation()
                    is Resource.Success -> {
                        hideLoginLoadingAnimation()
                        requireActivity().apply {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        hideLoginLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.login_error_blank_email)
                                    binding.tiLayoutEmail.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_PASSWORD) -> {
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.login_error_blank_password)
                                    binding.tiLayoutPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.login_error_email)
                                }
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { validateLoginInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(), requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND) -> {
                                    snackbar = showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.login_error_not_found),
                                        requireContext().getString(R.string.login_btn_register)
                                    ) { navigateToRegister() }
                                }
                                it.contains(ERROR_FIREBASE_AUTH_CREDENTIALS) -> {
                                    snackbar = showNormalSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.login_error_credentials)
                                    )
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(), requireView()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun googleOnClick() = binding.btnGoogle.setOnClickListener {
        launchGoogleSignInClient()
    }

    private fun launchGoogleSignInClient() = CoroutineScope(Dispatchers.Main).launch {
        try {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
                requestIdToken(BuildConfig.GOOGLE_AUTH_SERVER_CLIENT_ID)
                requestId()
                requestEmail()
            }.build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), options)
            googleSignInClient.signOut().await()

            googleResultLauncher.launch(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            // TODO EDIT
            Timber.e("await exception: $e")
        }
    }

    private val googleResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            // Got an ID token from Google. Use it to authenticate with Firebase.
            account?.let { viewModel.loginWithGoogleAccount(account) }
        } catch (e: Exception) {
            Timber.e("googleResultLauncher Exception: ${e.message}")
        }
    }

    private fun loginWithGoogleAccountObserver() =
        viewModel.loginWithGoogleAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showGoogleLoadingAnimation()
                    is Resource.Success -> {
                        hideGoogleLoadingAnimation()
                        requireActivity().apply {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        hideGoogleLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { validateLoginInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(), requireView()
                                    )
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(), requireView()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun twitterOnClick() = binding.btnTwitter.setOnClickListener {
        loginWithTwitterAccount()
    }

    private fun loginWithTwitterAccount() {
        showTwitterLoadingAnimation()

        val oAuthProvider = OAuthProvider.newBuilder(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
        oAuthProvider.addCustomParameter("lang", currentAppLanguage)

        firebaseAuth.startActivityForSignInWithProvider(requireActivity(), oAuthProvider.build())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val authCredential = it.result.credential
                    authCredential?.let { credential ->
                        viewModel.loginWithTwitterAccount(credential)
                    }
                } else {
                    // TODO EDIT
                    hideTwitterLoadingAnimation()
                    it.exception?.message?.let { message ->
                        Timber.e("else -> onFail:")
                        Timber.e("Exception: $message")
                        Timber.e("--------------------------------------------")
                        if (message.contains("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.")) {
                            Toast.makeText(
                                requireContext(),
                                "An account already exists with the same email address but different sign-in credentials.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }

    private fun loginWithTwitterAccountObserver() =
        viewModel.loginWithTwitterAccountLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showTwitterLoadingAnimation()
                    is Resource.Success -> {
                        hideTwitterLoadingAnimation()
                        requireActivity().apply {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        hideTwitterLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            snackbar = Snackbar.make(
                                requireView(), it,
                                BaseTransientBottomBar.LENGTH_INDEFINITE
                            ).apply {
                                setAction(requireContext().getString(R.string.error_btn_confirm)) { dismiss() }
                                show()
                            }
                        }
                    }
                }
            }
        }

    private fun facebookOnClick() = binding.btnFacebook.setOnClickListener {
        Toast.makeText(requireContext(), "facebook", Toast.LENGTH_SHORT).show()
    }

    private fun showLoginLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        btnLogin.isClickable = false
        btnGoogle.isClickable = false
        btnTwitter.isClickable = false
        btnFacebook.isClickable = false
        btnLogin.text = null
        cpiLogin.visibility = VISIBLE
    }

    private fun hideLoginLoadingAnimation() = binding.apply {
        cpiLogin.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        btnLogin.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnLogin.text = requireContext().getString(R.string.login_btn_login)
    }

    private fun showGoogleLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        btnLogin.isClickable = false
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
        btnLogin.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnGoogle.setIconResource(R.drawable.ic_auth_google)
    }

    private fun showTwitterLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        btnLogin.isClickable = false
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
        btnLogin.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnTwitter.setIconResource(R.drawable.ic_auth_twitter)
    }

    private fun showFacebookLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        btnLogin.isClickable = false
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
        btnLogin.isClickable = true
        btnGoogle.isClickable = true
        btnTwitter.isClickable = true
        btnFacebook.isClickable = true
        btnFacebook.setIconResource(R.drawable.ic_auth_facebook)
    }
}