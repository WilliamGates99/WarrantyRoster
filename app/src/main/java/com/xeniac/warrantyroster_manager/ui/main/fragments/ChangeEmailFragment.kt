package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType.*
import android.view.View
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentChangeEmailBinding
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_CREDENTIALS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_CHANGE_EMAIL_NEW_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_CHANGE_EMAIL_PASSWORD
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthAccountExists
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthCredentialsError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.Status
import com.xeniac.warrantyroster_manager.utils.UserHelper.isEmailValid
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeEmailFragment : Fragment(R.layout.fragment_change_email) {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var newEmail: String

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeEmailBinding.bind(view)

        textInputsBackgroundColor()
        textInputsStrokeColor()
        passwordInputsInputType()
        returnToMainActivity()
        changeEmailOnClick()
        changeEmailActionDone()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val password = binding.tiEditPassword.text.toString().trim()
            val newEmail = binding.tiEditNewEmail.text.toString().trim().lowercase()

            if (password.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_EMAIL_PASSWORD, password)
            }

            if (newEmail.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_EMAIL_NEW_EMAIL, newEmail)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_CHANGE_EMAIL_PASSWORD)?.let { restoredPassword ->
                binding.tiEditPassword.setText(restoredPassword)
            }

            it.getString(SAVE_INSTANCE_CHANGE_EMAIL_NEW_EMAIL)?.let { restoredNewEmail ->
                binding.tiEditNewEmail.setText(restoredNewEmail)
            }
        }
        super.onViewStateRestored(savedInstanceState)
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
        binding.tiEditPassword.addTextChangedListener {
            binding.tiLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        binding.tiEditNewEmail.addTextChangedListener {
            binding.tiLayoutNewEmail.isErrorEnabled = false
            binding.tiLayoutNewEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun passwordInputsInputType() {
        binding.apply {
            tiEditPassword.setOnFocusChangeListener { _, isFocused ->
                if (isFocused) {
                    tiEditPassword.inputType = TYPE_TEXT_VARIATION_PASSWORD
                } else {
                    tiEditPassword.inputType = TYPE_NULL
                }
            }
        }
    }

    private fun returnToMainActivity() = binding.toolbar.setNavigationOnClickListener {
        requireActivity().onBackPressed()
    }

    private fun changeEmailOnClick() = binding.btnChangeEmail.setOnClickListener {
        getChangeUserEmailInputs()
    }

    private fun changeEmailActionDone() =
        binding.tiEditNewEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getChangeUserEmailInputs()
            }
            false
        }

    private fun getChangeUserEmailInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val password = binding.tiEditPassword.text.toString().trim()
        newEmail = binding.tiEditNewEmail.text.toString().trim().lowercase()

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
            } else if (newEmail == FirebaseAuth.getInstance().currentUser?.email) {
                binding.tiLayoutNewEmail.requestFocus()
                binding.tiLayoutNewEmail.error =
                    requireContext().getString(R.string.change_email_error_email_same)
            } else {
                showLoadingAnimation()
                reAuthenticateUser(password)
            }
        }
    }

    private fun subscribeToObservers() {
        reAuthenticateUserObserver()
        changeUserEmailObserver()
    }

    private fun reAuthenticateUser(password: String) = viewModel.reAuthenticateUser(password)

    private fun reAuthenticateUserObserver() =
        viewModel.reAuthenticateUserLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> changeUserEmail()
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkConnectionError(
                                        requireContext(), binding.root
                                    ) { getChangeUserEmailInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {

                                    show403Error(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_AUTH_CREDENTIALS) -> {
                                    showFirebaseAuthCredentialsError(
                                        binding.root,
                                        requireContext().getString(R.string.change_email_error_credentials)
                                    )
                                }
                                else -> {
                                    showNetworkFailureError(requireContext(), binding.root)
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun changeUserEmail() = viewModel.changeUserEmail(newEmail)

    private fun changeUserEmailObserver() =
        viewModel.changeUserEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.change_email_dialog_message))
                            setCancelable(false)
                            setPositiveButton(requireContext().getString(R.string.change_email_dialog_positive)) { _, _ -> }
                            setOnDismissListener { requireActivity().onBackPressed() }
                            show()
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkConnectionError(
                                        requireContext(), binding.root
                                    ) { getChangeUserEmailInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS) -> {
                                    showFirebaseAuthAccountExists(
                                        binding.root,
                                        requireContext().getString(R.string.change_email_error_email_exists),
                                        requireContext().getString(R.string.error_btn_confirm)
                                    ) { snackbar?.dismiss() }
                                }
                                else -> {
                                    showNetworkFailureError(requireContext(), binding.root)
                                }
                            }
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
}