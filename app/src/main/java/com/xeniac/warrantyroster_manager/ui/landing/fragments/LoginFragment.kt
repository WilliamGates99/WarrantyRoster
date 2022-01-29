package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
import com.xeniac.warrantyroster_manager.ui.landing.LandingActivity
import com.xeniac.warrantyroster_manager.ui.landing.LandingViewModel
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.utils.Resource

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var viewModel: LandingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = (activity as LandingActivity).viewModel

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
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiEditEmail.addTextChangedListener {
            binding.tiLayoutEmail.isErrorEnabled = false
            binding.tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        binding.tiEditPassword.addTextChangedListener {
            binding.tiLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun forgotPwOnClick() = binding.btnForgotPw.setOnClickListener {
        navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    private fun registerOnClick() = binding.btnRegister.setOnClickListener {
        navigateToRegister()
    }

    private fun navigateToRegister() =
        navController.navigate(R.id.action_loginFragment_to_registerFragment)

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
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase()
        val password = binding.tiEditPassword.text.toString().trim()

        if (email.isBlank()) {
            binding.tiLayoutEmail.requestFocus()
            binding.tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (password.isBlank()) {
            binding.tiLayoutPassword.requestFocus()
            binding.tiLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiLayoutEmail.requestFocus()
                binding.tiLayoutEmail.error =
                    requireContext().getString(R.string.login_error_email)
            } else {
                loginViaEmail(email, password)
            }
        }
    }

    private fun loginViaEmail(email: String, password: String) =
        viewModel.loginViaEmail(email, password)

    private fun loginObserver() =
        viewModel.loginLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoadingAnimation()
                }
                is Resource.Success -> {
                    hideLoadingAnimation()

                    requireContext()
                        .getSharedPreferences(PREFERENCE_LOGIN, Context.MODE_PRIVATE).edit().apply {
                            putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true)
                            apply()
                        }

                    Intent(requireContext(), MainActivity::class.java).apply {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
                is Resource.Error -> {
                    hideLoadingAnimation()
                    response.message?.let {
                        when {
                            it.contains("Unable to connect to the internet") -> {
                                Snackbar.make(
                                    binding.root,
                                    requireContext().getString(R.string.network_error_connection),
                                    LENGTH_LONG
                                ).apply {
                                    setAction(requireContext().getString(R.string.network_error_retry)) { getLoginInputs() }
                                    show()
                                }
                            }
                            it.contains("There is no user record corresponding to this identifier") -> {
                                Snackbar.make(
                                    binding.root,
                                    requireContext().getString(R.string.login_error_not_found),
                                    LENGTH_LONG
                                ).apply {
                                    setAction(requireContext().getString(R.string.login_btn_register)) { navigateToRegister() }
                                    show()
                                }
                            }
                            it.contains("The password is invalid or the user does not have a password") -> {
                                Snackbar.make(
                                    binding.root,
                                    requireContext().getString(R.string.login_error_credentials),
                                    LENGTH_LONG
                                ).show()
                            }
                            else -> {
                                Snackbar.make(
                                    binding.root,
                                    requireContext().getString(R.string.network_error_failure),
                                    LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

            if (viewModel.loginLiveData.value != null) {
                viewModel.loginLiveData.value = null
            }
        }

    private fun showLoadingAnimation() {
        binding.tiEditEmail.isEnabled = false
        binding.tiEditPassword.isEnabled = false
        binding.btnLogin.isClickable = false
        binding.btnLogin.text = null
        binding.cpiLogin.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiLogin.visibility = GONE
        binding.tiEditEmail.isEnabled = true
        binding.tiEditPassword.isEnabled = true
        binding.btnLogin.isClickable = true
        binding.btnLogin.text =
            requireContext().getString(R.string.login_btn_login)
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
}