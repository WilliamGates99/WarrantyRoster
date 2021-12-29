package com.xeniac.warrantyroster_manager.mainactivity.changepasswordfragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentChangePasswordBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePasswordBinding.bind(view)
        (requireContext() as MainActivity).hideNavBar()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnToMainActivity()
        changePasswordOnClick()
        changePasswordActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiChangePasswordEditCurrent.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiChangePasswordLayoutCurrent.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiChangePasswordLayoutCurrent.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiChangePasswordEditNew.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiChangePasswordLayoutNew.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiChangePasswordLayoutNew.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiChangePasswordEditRetype.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiChangePasswordLayoutRetype.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiChangePasswordLayoutRetype.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiChangePasswordEditCurrent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputPassword: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiChangePasswordLayoutCurrent.isErrorEnabled = false
                binding.tiChangePasswordLayoutCurrent.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiChangePasswordEditNew.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputPassword: CharSequence?, start: Int, before: Int, count: Int
            ) {
                if (binding.tiChangePasswordLayoutNew.hasFocus()) {
                    when (newPasswordStrength(inputPassword.toString())) {
                        (-1).toByte() -> {
                            binding.tiChangePasswordLayoutNew.boxStrokeColor =
                                ContextCompat.getColor(requireContext(), R.color.red)
                            binding.tiChangePasswordLayoutNew.helperText =
                                getString(R.string.change_password_helper_password_weak)
                            binding.tiChangePasswordLayoutNew.setHelperTextColor(
                                ContextCompat.getColorStateList(requireContext(), R.color.red)
                            )
                        }
                        (0).toByte() -> {
                            binding.tiChangePasswordLayoutNew.boxStrokeColor =
                                ContextCompat.getColor(requireContext(), R.color.orange)
                            binding.tiChangePasswordLayoutNew.helperText =
                                getString(R.string.change_password_helper_password_mediocre)
                            binding.tiChangePasswordLayoutNew.setHelperTextColor(
                                ContextCompat.getColorStateList(requireContext(), R.color.orange)
                            )
                        }
                        (1).toByte() -> {
                            binding.tiChangePasswordLayoutNew.boxStrokeColor =
                                ContextCompat.getColor(requireContext(), R.color.green)
                            binding.tiChangePasswordLayoutNew.helperText =
                                getString(R.string.change_password_helper_password_strong)
                            binding.tiChangePasswordLayoutNew.setHelperTextColor(
                                ContextCompat.getColorStateList(requireContext(), R.color.green)
                            )
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiChangePasswordEditRetype.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputPassword: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiChangePasswordLayoutCurrent.isErrorEnabled = false
                binding.tiChangePasswordLayoutCurrent.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun returnToMainActivity() {
        binding.toolbarChangePassword.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun changePasswordOnClick() {
        binding.btnChangePassword.setOnClickListener {
            changeUserPassword()
        }
    }

    private fun changePasswordActionDone() {
        binding.tiChangePasswordEditRetype.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeUserPassword()
            }
            false
        }
    }

    private fun changeUserPassword() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (NetworkHelper.hasNetworkAccess(requireContext())) {
            getChangeUserPasswordInputs()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { changeUserPassword() }
                show()
            }
        }
    }

    private fun getChangeUserPasswordInputs() {
        val currentPassword = binding.tiChangePasswordEditCurrent.text.toString().trim()
        val newPassword = binding.tiChangePasswordEditNew.text.toString().trim()
        val retypeNewPassword = binding.tiChangePasswordEditRetype.text.toString().trim()

        if (currentPassword.isBlank()) {
            binding.tiChangePasswordLayoutCurrent.requestFocus()
            binding.tiChangePasswordLayoutCurrent.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (newPassword.isBlank()) {
            binding.tiChangePasswordLayoutNew.requestFocus()
            binding.tiChangePasswordLayoutNew.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (retypeNewPassword.isBlank()) {
            binding.tiChangePasswordLayoutRetype.requestFocus()
            binding.tiChangePasswordLayoutRetype.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (newPasswordStrength(newPassword) == (-1).toByte()) {
                binding.tiChangePasswordLayoutNew.requestFocus()
                binding.tiChangePasswordLayoutNew.error =
                    requireContext().getString(R.string.change_password_error_password_short)
            } else if (!isRetypePasswordValid(newPassword, retypeNewPassword)) {
                binding.tiChangePasswordLayoutRetype.requestFocus()
                binding.tiChangePasswordLayoutRetype.error =
                    requireContext().getString(R.string.change_password_error_match)
            } else {
                showLoadingAnimation()
                reAuthenticateUser(currentPassword, newPassword)
            }
        }
    }

    private fun reAuthenticateUser(currentPassword: String, newPassword: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentUser?.let {
                    val credential = EmailAuthProvider
                        .getCredential(it.email.toString(), currentPassword)
                    it.reauthenticate(credential).await()
                    Log.i("reAuthenticateUser", "User re-authenticated.")
                    changeUserPasswordAuth(newPassword)
                }
            } catch (e: Exception) {
                Log.e("reAuthenticateUser", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    if (e.toString()
                            .contains("The password is invalid or the user does not have a password")
                    ) {
                        Snackbar.make(
                            binding.root,
                            requireContext().getString(R.string.change_email_error_credentials),
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

    private fun changeUserPasswordAuth(newPassword: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentUser?.let {
                    it.updatePassword(newPassword).await()
                    Log.i("changeUserPasswordAuth", "User password updated.")

                    withContext(Dispatchers.Main) {
                        hideLoadingAnimation()
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.change_password_dialog_message))
                            setPositiveButton(requireContext().getString(R.string.change_password_dialog_positive)) { _, _ -> }
                            setOnDismissListener { requireActivity().onBackPressed() }
                            show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("changeUserPasswordAuth", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    Snackbar.make(
                        binding.root,
                        requireContext().getString(R.string.network_error_failure),
                        LENGTH_LONG
                    ).show()
                }
            }
        }

    private fun showLoadingAnimation() {
        binding.tiChangePasswordEditCurrent.isEnabled = false
        binding.tiChangePasswordEditNew.isEnabled = false
        binding.tiChangePasswordEditRetype.isEnabled = false
        binding.btnChangePassword.isClickable = false
        binding.btnChangePassword.text = null
        binding.cpiChangePassword.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiChangePassword.visibility = GONE
        binding.tiChangePasswordEditCurrent.isEnabled = true
        binding.tiChangePasswordEditNew.isEnabled = true
        binding.tiChangePasswordEditRetype.isEnabled = true
        binding.btnChangePassword.isClickable = true
        binding.btnChangePassword.text =
            requireContext().getString(R.string.change_password_btn_change)
    }

    private fun newPasswordStrength(password: String): Byte {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=(.*[\\W])*).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return when {
            password.length < 6 -> -1
            password.length < 8 -> 0
            else -> if (passwordMatcher.matches(password)) 1 else 0
        }
    }

    private fun isRetypePasswordValid(password: String, retypePassword: String): Boolean =
        password == retypePassword
}