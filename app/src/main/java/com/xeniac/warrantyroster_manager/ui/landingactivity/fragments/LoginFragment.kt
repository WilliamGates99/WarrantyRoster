package com.xeniac.warrantyroster_manager.ui.landingactivity.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
import com.xeniac.warrantyroster_manager.ui.mainactivity.MainActivity
import com.xeniac.warrantyroster_manager.util.Constants.Companion.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.util.Constants.Companion.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.util.NetworkHelper.Companion.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        textInputsBackgroundColor()
        textInputsStrokeColor()
        forgotPwOnClick()
        registerOnClick()
        loginOnClick()
        loginActionDone()
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
        binding.tiEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiLayoutEmail.isErrorEnabled = false
                binding.tiLayoutEmail.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiLayoutPassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun forgotPwOnClick() = binding.btnForgotPw.setOnClickListener {
        navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    private fun registerOnClick() = binding.btnRegister.setOnClickListener {
        navigateToRegister()
    }

    private fun navigateToRegister() {
        navController.navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun loginOnClick() = binding.btnLogin.setOnClickListener {
        loginViaEmail()
    }

    private fun loginActionDone() =
        binding.tiEditPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViaEmail()
            }
            false
        }

    private fun loginViaEmail() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (hasInternetConnection(requireContext())) {
            getLoginInputs()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { getLoginInputs() }
                show()
            }
        }
    }

    private fun getLoginInputs() {
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
                loginViaEmailAuth(email, password)
            }
        }
    }

    private fun loginViaEmailAuth(email: String, password: String) {
        showLoadingAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await().apply {
                    user?.let {
                        Log.i("loginViaEmail", "${it.email} logged in successfully.")

                        requireContext().getSharedPreferences(
                            PREFERENCE_LOGIN, Context.MODE_PRIVATE
                        ).edit().apply {
                            putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true)
                            apply()
                        }

                        withContext(Dispatchers.Main) {
                            hideLoadingAnimation()
                            Intent(requireContext(), MainActivity::class.java).apply {
                                startActivity(this)
                                requireActivity().finish()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("loginViaEmail", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    when {
                        e.toString()
                            .contains("There is no user record corresponding to this identifier") -> {
                            Snackbar.make(
                                binding.root,
                                requireContext().getString(R.string.login_error_not_found),
                                LENGTH_INDEFINITE
                            ).apply {
                                setAction(requireContext().getString(R.string.login_btn_register)) { navigateToRegister() }
                                show()
                            }
                        }
                        e.toString()
                            .contains("The password is invalid or the user does not have a password") -> {
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