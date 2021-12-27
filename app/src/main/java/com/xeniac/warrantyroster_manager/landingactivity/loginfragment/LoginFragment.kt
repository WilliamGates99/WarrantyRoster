package com.xeniac.warrantyroster_manager.landingactivity.loginfragment

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
import com.xeniac.warrantyroster_manager.Constants
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLoginBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
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
        binding.tiLoginEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLoginLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLoginLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiLoginEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLoginLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLoginLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiLoginEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiLoginLayoutEmail.isErrorEnabled = false
                binding.tiLoginLayoutEmail.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiLoginEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiLoginLayoutPassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun forgotPwOnClick() {
        binding.btnLoginForgotPw.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun registerOnClick() {
        binding.btnLoginRegister.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginOnClick() {
        binding.btnLoginLogin.setOnClickListener {
            loginViaEmail()
        }
    }

    private fun loginActionDone() {
        binding.tiLoginEditPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViaEmail()
            }
            false
        }
    }

    private fun loginViaEmail() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (NetworkHelper.hasNetworkAccess(requireContext())) {
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
        val email = binding.tiLoginEditEmail.text.toString().trim().lowercase()
        val password = binding.tiLoginEditPassword.text.toString().trim()

        if (email.isBlank()) {
            binding.tiLoginLayoutEmail.requestFocus()
            binding.tiLoginLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (password.isBlank()) {
            binding.tiLoginLayoutPassword.requestFocus()
            binding.tiLoginLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiLoginLayoutEmail.requestFocus()
                binding.tiLoginLayoutEmail.error =
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
                            Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE
                        ).edit().apply {
                            putBoolean(Constants.PREFERENCE_IS_LOGGED_IN_KEY, true)
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
                    if (e.toString().contains(
                            "The password is invalid or the user does not have a password"
                        )
                    ) {
                        Snackbar.make(
                            binding.root,
                            requireContext().getString(R.string.login_error_credentials),
                            LENGTH_LONG
                        ).show()
                    } else {
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

    private fun showLoadingAnimation() {
        binding.tiLoginEditEmail.isEnabled = false
        binding.tiLoginEditPassword.isEnabled = false
        binding.btnLoginLogin.isClickable = false
        binding.btnLoginLogin.text = null
        binding.cpiLogin.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.tiLoginEditEmail.isEnabled = true
        binding.tiLoginEditPassword.isEnabled = true
        binding.btnLoginLogin.isClickable = true
        binding.btnLoginLogin.text =
            requireContext().getString(R.string.login_btn_login)
        binding.cpiLogin.visibility = GONE
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}