package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Context
import android.os.Build
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentChangePasswordBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.ui.viewmodels.ChangePasswordViewModel
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper.showOneBtnAlertDialog
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_CREDENTIALS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_CHANGE_PASSWORD_CONFIRM_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_CHANGE_PASSWORD_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNormalSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import com.xeniac.warrantyroster_manager.util.UserHelper.passwordStrength
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: ChangePasswordViewModel

    private lateinit var newPassword: String

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePasswordBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[ChangePasswordViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        toolbarNavigationBackOnClick()
        subscribeToObservers()
        changePasswordOnClick()
        changePasswordActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val currentPassword = binding.tiEditCurrentPassword.text.toString().trim()
            val newPassword = binding.tiEditNewPassword.text.toString().trim()
            val retypeNewPassword = binding.tiEditConfirmNewPassword.text.toString().trim()

            if (currentPassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT_PASSWORD, currentPassword)
            }

            if (newPassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_CHANGE_PASSWORD_NEW_PASSWORD, newPassword)
            }

            if (retypeNewPassword.isNotBlank()) {
                outState.putString(
                    SAVE_INSTANCE_CHANGE_PASSWORD_CONFIRM_NEW_PASSWORD,
                    retypeNewPassword
                )
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_CHANGE_PASSWORD_CURRENT_PASSWORD)
                ?.let { restoredCurrentPassword ->
                    binding.tiEditCurrentPassword.setText(restoredCurrentPassword)
                }

            it.getString(SAVE_INSTANCE_CHANGE_PASSWORD_NEW_PASSWORD)?.let { restoredNewPassword ->
                binding.tiEditNewPassword.setText(restoredNewPassword)
            }

            it.getString(SAVE_INSTANCE_CHANGE_PASSWORD_CONFIRM_NEW_PASSWORD)
                ?.let { restoredRetypePassword ->
                    binding.tiEditConfirmNewPassword.setText(restoredRetypePassword)
                }
        }
        super.onViewStateRestored(savedInstanceState)
    }

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        }
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

        tiEditConfirmNewPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutConfirmNewPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutConfirmNewPassword.boxBackgroundColor =
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
            tiLayoutNewPassword.isErrorEnabled = false

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

        tiEditConfirmNewPassword.addTextChangedListener {
            tiLayoutCurrentPassword.isErrorEnabled = false
            tiLayoutCurrentPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun toolbarNavigationBackOnClick() = binding.toolbar.setNavigationOnClickListener {
        navigateBack()
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun subscribeToObservers() {
        checkInputsObserver()
        reAuthenticateUserObserver()
        changeUserPasswordObserver()
    }

    private fun changePasswordOnClick() = binding.btnChangePassword.setOnClickListener {
        validateChangeUserPasswordInputs()
    }

    private fun changePasswordActionDone() =
        binding.tiEditConfirmNewPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateChangeUserPasswordInputs()
            }
            false
        }

    private fun validateChangeUserPasswordInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            val currentPassword = binding.tiEditCurrentPassword.text.toString().trim()
            newPassword = binding.tiEditNewPassword.text.toString().trim()
            val retypeNewPassword = binding.tiEditConfirmNewPassword.text.toString().trim()

            viewModel.validateChangePasswordInputs(currentPassword, newPassword, retypeNewPassword)
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { validateChangeUserPasswordInputs() }
            Timber.e("validateChangeUserPasswordInputs Error: Offline")
        }
    }

    private fun checkInputsObserver() =
        viewModel.checkInputsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        /* NO-OP */
                    }
                    is Resource.Success -> reAuthenticateUser(response.data.toString())
                    is Resource.Error -> {
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_PASSWORD) -> {
                                    binding.tiLayoutCurrentPassword.error =
                                        requireContext().getString(R.string.change_password_error_blank_current)
                                    binding.tiLayoutCurrentPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_NEW_PASSWORD) -> {
                                    binding.tiLayoutNewPassword.error =
                                        requireContext().getString(R.string.change_password_error_blank_new)
                                    binding.tiLayoutNewPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_RETYPE_PASSWORD) -> {
                                    binding.tiLayoutConfirmNewPassword.error =
                                        requireContext().getString(R.string.change_password_error_blank_confirm)
                                    binding.tiLayoutConfirmNewPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_PASSWORD_SHORT) -> {
                                    binding.tiLayoutNewPassword.requestFocus()
                                    binding.tiLayoutNewPassword.error =
                                        requireContext().getString(R.string.change_password_error_password_short)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_NOT_MATCH) -> {
                                    binding.tiLayoutConfirmNewPassword.requestFocus()
                                    binding.tiLayoutConfirmNewPassword.error =
                                        requireContext().getString(R.string.change_password_error_match)
                                }
                                else -> {
                                    snackbar = showSomethingWentWrongError(
                                        requireContext(), requireView()
                                    )
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
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> changeUserPassword()
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(), requireView()
                                    )
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
                                    snackbar = showNormalSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.change_password_error_credentials)
                                    )
                                }
                                else -> {
                                    snackbar = showSomethingWentWrongError(
                                        requireContext(), requireView()
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
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        showOneBtnAlertDialog(
                            requireContext(),
                            R.string.change_password_dialog_message,
                            R.string.change_password_dialog_positive,
                        ) { navigateBack() }
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                else -> showSomethingWentWrongError(requireContext(), requireView())
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        tiEditCurrentPassword.isEnabled = false
        tiEditNewPassword.isEnabled = false
        tiEditConfirmNewPassword.isEnabled = false
        btnChangePassword.isClickable = false
        btnChangePassword.text = null
        cpiChangePassword.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiChangePassword.visibility = GONE
        tiEditCurrentPassword.isEnabled = true
        tiEditNewPassword.isEnabled = true
        tiEditConfirmNewPassword.isEnabled = true
        btnChangePassword.isClickable = true
        btnChangePassword.text = requireContext().getString(R.string.change_password_btn_change)
    }
}