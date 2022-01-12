package com.xeniac.warrantyroster_manager.mainactivity.changeemailfragment

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.util.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentChangeEmailBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChangeEmailFragment : Fragment(R.layout.fragment_change_email) {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeEmailBinding.bind(view)
        (requireContext() as MainActivity).hideNavBar()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnToMainActivity()
        changeEmailOnClick()
        changeEmailActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditNewEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutNewEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutNewEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputPassword: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiLayoutPassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiEditNewEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiLayoutNewEmail.isErrorEnabled = false
                binding.tiLayoutNewEmail.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun returnToMainActivity() = binding.toolbar.setNavigationOnClickListener {
        requireActivity().onBackPressed()
    }

    private fun changeEmailOnClick() = binding.btnChangeEmail.setOnClickListener {
        changeUserEmail()
    }

    private fun changeEmailActionDone() =
        binding.tiEditNewEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeUserEmail()
            }
            false
        }

    private fun changeUserEmail() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (NetworkHelper.hasNetworkAccess(requireContext())) {
            getChangeUserEmailInputs()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { changeUserEmail() }
                show()
            }
        }
    }

    private fun getChangeUserEmailInputs() {
        val password = binding.tiEditPassword.text.toString().trim()
        val newEmail = binding.tiEditNewEmail.text.toString().trim().lowercase()

        if (password.isBlank()) {
            binding.tiLayoutPassword.requestFocus()
            binding.tiLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (newEmail.isBlank()) {
            binding.tiLayoutNewEmail.requestFocus()
            binding.tiLayoutNewEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(newEmail)) {
                binding.tiLayoutNewEmail.requestFocus()
                binding.tiLayoutNewEmail.error =
                    requireContext().getString(R.string.change_email_error_new_email)
            } else if (newEmail == currentUser?.email) {
                binding.tiLayoutNewEmail.requestFocus()
                binding.tiLayoutNewEmail.error =
                    requireContext().getString(R.string.change_email_error_email_same)
            } else {
                showLoadingAnimation()
                reAuthenticateUser(password, newEmail)
            }
        }
    }

    private fun reAuthenticateUser(password: String, newEmail: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentUser?.let {
                    val credential = EmailAuthProvider.getCredential(it.email.toString(), password)
                    it.reauthenticate(credential).await()
                    Log.i("reAuthenticateUser", "User re-authenticated.")
                    changeUserEmailAuth(newEmail)
                }
            } catch (e: Exception) {
                Log.e("reAuthenticateUser", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    when {
                        e.toString()
                            .contains("The password is invalid or the user does not have a password") -> {
                            Snackbar.make(
                                binding.root,
                                requireContext().getString(R.string.change_email_error_credentials),
                                LENGTH_INDEFINITE
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

    private fun changeUserEmailAuth(newEmail: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentUser?.let {
                    it.updateEmail(newEmail).await()
                    Log.i("changeUserEmailAuth", "User email updated to ${newEmail}.")

                    withContext(Dispatchers.Main) {
                        hideLoadingAnimation()
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.change_email_dialog_message))
                            setPositiveButton(requireContext().getString(R.string.change_email_dialog_positive)) { _, _ -> }
                            setOnDismissListener { requireActivity().onBackPressed() }
                            show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("changeUserEmailAuth", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    when {
                        e.toString()
                            .contains("The email address is already in use by another account") -> {
                            Snackbar.make(
                                binding.root,
                                requireContext().getString(R.string.change_email_error_email_exists),
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

    private fun showLoadingAnimation() {
        binding.tiEditPassword.isEnabled = false
        binding.tiEditNewEmail.isEnabled = false
        binding.btnChangeEmail.isClickable = false
        binding.btnChangeEmail.text = null
        binding.cpiChangeEmail.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiChangeEmail.visibility = GONE
        binding.tiEditPassword.isEnabled = true
        binding.tiEditNewEmail.isEnabled = true
        binding.btnChangeEmail.isClickable = true
        binding.btnChangeEmail.text = requireContext().getString(R.string.change_email_btn_change)
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
}