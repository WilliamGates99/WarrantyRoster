package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentChangePasswordBinding
import com.xeniac.warrantyroster_manager.models.Status
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    private lateinit var newPassword: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePasswordBinding.bind(view)
        viewModel = (activity as MainActivity).settingsViewModel

        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnToMainActivity()
        changePasswordOnClick()
        changePasswordActionDone()
        reAuthenticateUserObserver()
        changeUserPasswordObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiEditCurrentPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutCurrentPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutCurrentPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditNewPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutNewPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutNewPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditRetypePassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiEditCurrentPassword.addTextChangedListener {
            binding.tiLayoutCurrentPassword.isErrorEnabled = false
            binding.tiLayoutCurrentPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        binding.tiEditNewPassword.addTextChangedListener { inputPassword ->
            if (binding.tiLayoutNewPassword.hasFocus()) {
                when (newPasswordStrength(inputPassword.toString())) {
                    (-1).toByte() -> {
                        binding.tiLayoutNewPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.red)
                        binding.tiLayoutNewPassword.helperText =
                            getString(R.string.change_password_helper_password_weak)
                        binding.tiLayoutNewPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.red)
                        )
                    }
                    (0).toByte() -> {
                        binding.tiLayoutNewPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        binding.tiLayoutNewPassword.helperText =
                            getString(R.string.change_password_helper_password_mediocre)
                        binding.tiLayoutNewPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.orange)
                        )
                    }
                    (1).toByte() -> {
                        binding.tiLayoutNewPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.green)
                        binding.tiLayoutNewPassword.helperText =
                            getString(R.string.change_password_helper_password_strong)
                        binding.tiLayoutNewPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.green)
                        )
                    }
                }
            }
        }

        binding.tiEditRetypePassword.addTextChangedListener {
            binding.tiLayoutCurrentPassword.isErrorEnabled = false
            binding.tiLayoutCurrentPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun returnToMainActivity() = binding.toolbar.setNavigationOnClickListener {
        requireActivity().onBackPressed()
    }

    private fun changePasswordOnClick() = binding.btnChangePassword.setOnClickListener {
        getChangeUserPasswordInputs()
    }

    private fun changePasswordActionDone() =
        binding.tiEditRetypePassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getChangeUserPasswordInputs()
            }
            false
        }

    private fun getChangeUserPasswordInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val currentPassword = binding.tiEditCurrentPassword.text.toString().trim()
        newPassword = binding.tiEditNewPassword.text.toString().trim()
        val retypeNewPassword = binding.tiEditRetypePassword.text.toString().trim()

        if (currentPassword.isBlank()) {
            binding.tiLayoutCurrentPassword.requestFocus()
            binding.tiLayoutCurrentPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (newPassword.isBlank()) {
            binding.tiLayoutNewPassword.requestFocus()
            binding.tiLayoutNewPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (retypeNewPassword.isBlank()) {
            binding.tiLayoutRetypePassword.requestFocus()
            binding.tiLayoutRetypePassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (newPasswordStrength(newPassword) == (-1).toByte()) {
                binding.tiLayoutNewPassword.requestFocus()
                binding.tiLayoutNewPassword.error =
                    requireContext().getString(R.string.change_password_error_password_short)
            } else if (!isRetypePasswordValid(newPassword, retypeNewPassword)) {
                binding.tiLayoutRetypePassword.requestFocus()
                binding.tiLayoutRetypePassword.error =
                    requireContext().getString(R.string.change_password_error_match)
            } else {
                showLoadingAnimation()
                reAuthenticateUser(currentPassword)
            }
        }
    }

    private fun reAuthenticateUser(currentPassword: String) =
        viewModel.reAuthenticateUser(currentPassword)

    private fun reAuthenticateUserObserver() =
        viewModel.reAuthenticateUserLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        changeUserPassword()
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(Constants.ERROR_NETWORK_CONNECTION) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_connection),
                                        LENGTH_LONG
                                    ).apply {
                                        setAction(requireContext().getString(R.string.network_error_retry)) {
                                            getChangeUserPasswordInputs()
                                        }
                                        show()
                                    }
                                }
                                it.contains(Constants.ERROR_FIREBASE_AUTH_CREDENTIALS) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.change_password_error_credentials),
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
        }

    private fun changeUserPassword() = viewModel.changeUserPassword(newPassword)

    private fun changeUserPasswordObserver() =
        viewModel.changeUserPasswordLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.change_password_dialog_message))
                            setPositiveButton(requireContext().getString(R.string.change_password_dialog_positive)) { _, _ -> }
                            setOnDismissListener { requireActivity().onBackPressed() }
                            show()
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(Constants.ERROR_NETWORK_CONNECTION) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_connection),
                                        LENGTH_LONG
                                    ).apply {
                                        setAction(requireContext().getString(R.string.network_error_retry)) {
                                            getChangeUserPasswordInputs()
                                        }
                                        show()
                                    }
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
        }

    private fun showLoadingAnimation() {
        binding.tiEditCurrentPassword.isEnabled = false
        binding.tiEditNewPassword.isEnabled = false
        binding.tiEditRetypePassword.isEnabled = false
        binding.btnChangePassword.isClickable = false
        binding.btnChangePassword.text = null
        binding.cpiChangePassword.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiChangePassword.visibility = GONE
        binding.tiEditCurrentPassword.isEnabled = true
        binding.tiEditNewPassword.isEnabled = true
        binding.tiEditRetypePassword.isEnabled = true
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