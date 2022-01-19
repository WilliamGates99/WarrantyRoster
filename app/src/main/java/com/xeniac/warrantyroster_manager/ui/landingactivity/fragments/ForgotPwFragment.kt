package com.xeniac.warrantyroster_manager.ui.landingactivity.fragments

import android.content.Context
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
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
import com.xeniac.warrantyroster_manager.util.NetworkHelper.Companion.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ForgotPwFragment : Fragment(R.layout.fragment_forgot_pw) {

    private var _binding: FragmentForgotPwBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwBinding.bind(view)
        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnOnClick()
        sendOnClick()
        sendActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun textInputsBackgroundColor() =
        binding.tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

    private fun textInputsStrokeColor() =
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

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        requireActivity().onBackPressed()
    }

    private fun sendOnClick() = binding.btnSend.setOnClickListener {
        sendResetPasswordEmail()
    }

    private fun sendActionDone() =
        binding.tiEditEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendResetPasswordEmail()
            }
            false
        }

    private fun sendResetPasswordEmail() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (hasInternetConnection(requireContext())) {
            getResetPasswordInput()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { sendResetPasswordEmail() }
                show()
            }
        }
    }

    private fun getResetPasswordInput() {
        val email = binding.tiEditEmail.text.toString().trim().lowercase()

        if (email.isBlank()) {
            binding.tiLayoutEmail.requestFocus()
            binding.tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiLayoutEmail.requestFocus()
                binding.tiLayoutEmail.error =
                    requireContext().getString(R.string.forgot_pw_error_email)
            } else {
                sendResetPasswordEmailAuth(email)
            }
        }
    }

    private fun sendResetPasswordEmailAuth(email: String) {
        showLoadingAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Log.i(
                    "sendResetPasswordEmail",
                    "Reset password email successfully sent to ${email}."
                )
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    val action = ForgotPwFragmentDirections
                        .actionForgotPasswordFragmentToForgotPwSentFragment(email)
                    navController.navigate(action)
                }
            } catch (e: Exception) {
                Log.e("sendResetPasswordEmail", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    when {
                        e.toString()
                            .contains("There is no user record corresponding to this identifier") -> {
                            Snackbar.make(
                                binding.root,
                                requireContext().getString(R.string.forgot_pw_error_not_found),
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
        binding.btnSend.isClickable = false
        binding.btnSend.text = null
        binding.cpiSend.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiSend.visibility = GONE
        binding.tiEditEmail.isEnabled = true
        binding.btnSend.isClickable = true
        binding.btnSend.text =
            requireContext().getString(R.string.login_btn_login)
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
}