package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_CREDENTIALS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_LOGIN_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_LOGIN_PASSWORD
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthAccountNotFoundError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthCredentialsError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: LandingViewModel

    var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        textInputsBackgroundColor()
        textInputsStrokeColor()
        forgotPwOnClick()
        registerOnClick()
        loginOnClick()
        loginActionDone()
        loginObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
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

    private fun forgotPwOnClick() = binding.btnForgotPw.setOnClickListener {
        navigateToForgotPw()
    }

    private fun registerOnClick() = binding.btnRegister.setOnClickListener {
        navigateToRegister()
    }

    private fun navigateToForgotPw() = findNavController()
        .navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())

    private fun navigateToRegister() = findNavController()
        .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())

    private fun loginOnClick() = binding.btnLogin.setOnClickListener {
        getLoginInputs()
    }

    private fun loginActionDone() =
        binding.tiEditPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getLoginInputs()
            }
            false
        }

    private fun getLoginInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)
        val password = binding.tiEditPassword.text.toString().trim()

        viewModel.checkLoginInputs(email, password)
    }

    private fun loginObserver() =
        viewModel.loginLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        Intent(requireContext(), MainActivity::class.java).apply {
                            startActivity(this)
                            requireActivity().finish()
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.login_error_blank_email)
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_PASSWORD) -> {
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.login_error_blank_password)
                                    binding.tiLayoutPassword.requestFocus()
                                    binding.tiLayoutPassword.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.login_error_email)
                                }
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { getLoginInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND) -> {
                                    snackbar = showFirebaseAuthAccountNotFoundError(
                                        requireView(),
                                        requireContext().getString(R.string.login_error_not_found),
                                        requireContext().getString(R.string.login_btn_register)
                                    ) { navigateToRegister() }
                                }
                                it.contains(ERROR_FIREBASE_AUTH_CREDENTIALS) -> {
                                    snackbar = showFirebaseAuthCredentialsError(
                                        requireView(),
                                        requireContext().getString(R.string.login_error_credentials)
                                    )
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        btnLogin.isClickable = false
        btnLogin.text = null
        cpiLogin.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiLogin.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        btnLogin.isClickable = true
        btnLogin.text = requireContext().getString(R.string.login_btn_login)
    }
}