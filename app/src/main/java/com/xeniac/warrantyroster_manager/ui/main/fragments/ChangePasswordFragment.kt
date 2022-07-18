package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentChangePasswordBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_CREDENTIALS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_CHANGE_PASSWORD_NEW
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_CHANGE_PASSWORD_RETYPE
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthCredentialsError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.Status
import com.xeniac.warrantyroster_manager.utils.UserHelper.passwordStrength
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: SettingsViewModel

    private lateinit var newPassword: String

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePasswordBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        onBackPressed()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnToMainActivity()
        changePasswordOnClick()
        changePasswordActionDone()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val currentPassword = binding.tiEditCurrentPassword.text.toString().trim()
            val newPassword = binding.tiEditNewPassword.text.toString().trim()
            val retypeNewPassword = binding.tiEditRetypePassword.text.toString().trim()

            if (currentPassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT, currentPassword)
            }

            if (newPassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_PASSWORD_NEW, newPassword)
            }

            if (retypeNewPassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_PASSWORD_RETYPE, retypeNewPassword)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT)?.let { restoredCurrentPassword ->
                binding.tiEditCurrentPassword.setText(restoredCurrentPassword)
            }

            it.getString(SAVE_INSTANCE_CHANGE_PASSWORD_NEW)?.let { restoredNewPassword ->
                binding.tiEditNewPassword.setText(restoredNewPassword)
            }

            it.getString(SAVE_INSTANCE_CHANGE_PASSWORD_RETYPE)?.let { restoredRetypePassword ->
                binding.tiEditRetypePassword.setText(restoredRetypePassword)
            }
        }
        super.onViewStateRestored(savedInstanceState)
    }

    private fun textInputsBackgroundColor() = binding.apply {
        tiEditCurrentPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutCurrentPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutCurrentPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditNewPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutNewPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutNewPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditRetypePassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditCurrentPassword.addTextChangedListener {
            tiLayoutCurrentPassword.isErrorEnabled = false
            tiLayoutCurrentPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        tiEditNewPassword.addTextChangedListener { inputPassword ->
            if (tiLayoutNewPassword.hasFocus()) {
                when (passwordStrength(inputPassword.toString())) {
                    (-1).toByte() -> {
                        tiLayoutNewPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.red)
                        tiLayoutNewPassword.helperText =
                            getString(R.string.change_password_helper_password_weak)
                        tiLayoutNewPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.red)
                        )
                    }
                    (0).toByte() -> {
                        tiLayoutNewPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        tiLayoutNewPassword.helperText =
                            getString(R.string.change_password_helper_password_mediocre)
                        tiLayoutNewPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.orange)
                        )
                    }
                    (1).toByte() -> {
                        tiLayoutNewPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.green)
                        tiLayoutNewPassword.helperText =
                            getString(R.string.change_password_helper_password_strong)
                        tiLayoutNewPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.green)
                        )
                    }
                }
            }
        }

        tiEditRetypePassword.addTextChangedListener {
            tiLayoutCurrentPassword.isErrorEnabled = false
            tiLayoutCurrentPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun returnToMainActivity() = binding.toolbar.setNavigationOnClickListener {
        findNavController().popBackStack()
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

    private fun subscribeToObservers() {
        checkInputsObserver()
        reAuthenticateUserObserver()
        changeUserPasswordObserver()
    }

    private fun getChangeUserPasswordInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        val currentPassword = binding.tiEditCurrentPassword.text.toString().trim()
        newPassword = binding.tiEditNewPassword.text.toString().trim()
        val retypeNewPassword = binding.tiEditRetypePassword.text.toString().trim()

        viewModel.checkChangePasswordInputs(currentPassword, newPassword, retypeNewPassword)
    }

    private fun checkInputsObserver() =
        viewModel.checkInputsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> {
                        /* NO-OP */
                    }
                    Status.SUCCESS -> {
                        reAuthenticateUser(response.data.toString())
                    }
                    Status.ERROR -> {
                        response.message?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_PASSWORD) -> {
                                    binding.tiLayoutCurrentPassword.requestFocus()
                                    binding.tiLayoutCurrentPassword.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_NEW_PASSWORD) -> {
                                    binding.tiLayoutNewPassword.requestFocus()
                                    binding.tiLayoutNewPassword.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_RETYPE_PASSWORD) -> {
                                    binding.tiLayoutRetypePassword.requestFocus()
                                    binding.tiLayoutRetypePassword.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_SHORT) -> {
                                    binding.tiLayoutNewPassword.requestFocus()
                                    binding.tiLayoutNewPassword.error =
                                        requireContext().getString(R.string.change_password_error_password_short)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_NOT_MATCH) -> {
                                    binding.tiLayoutRetypePassword.requestFocus()
                                    binding.tiLayoutRetypePassword.error =
                                        requireContext().getString(R.string.change_password_error_match)
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun reAuthenticateUser(password: String) = viewModel.reAuthenticateUser(password)

    private fun reAuthenticateUserObserver() =
        viewModel.reAuthenticateUserLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> changeUserPassword()
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { getChangeUserPasswordInputs() }
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
                                it.contains(ERROR_FIREBASE_AUTH_CREDENTIALS) -> {
                                    snackbar = showFirebaseAuthCredentialsError(
                                        requireView(),
                                        requireContext().getString(R.string.change_password_error_credentials)
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
                            setCancelable(false)
                            setPositiveButton(requireContext().getString(R.string.change_password_dialog_positive)) { _, _ -> }
                            setOnDismissListener { findNavController().popBackStack() }
                            show()
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { getChangeUserPasswordInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        tiEditCurrentPassword.isEnabled = false
        tiEditNewPassword.isEnabled = false
        tiEditRetypePassword.isEnabled = false
        btnChangePassword.isClickable = false
        btnChangePassword.text = null
        cpiChangePassword.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiChangePassword.visibility = GONE
        tiEditCurrentPassword.isEnabled = true
        tiEditNewPassword.isEnabled = true
        tiEditRetypePassword.isEnabled = true
        btnChangePassword.isClickable = true
        btnChangePassword.text = requireContext().getString(R.string.change_password_btn_change)
    }
}