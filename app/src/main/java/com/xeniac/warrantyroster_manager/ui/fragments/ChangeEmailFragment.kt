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
import com.xeniac.warrantyroster_manager.databinding.FragmentChangeEmailBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.ui.viewmodels.ChangeEmailViewModel
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper.showOneBtnAlertDialog
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_CREDENTIALS
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_SAME
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_CHANGE_EMAIL_NEW_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.SAVE_INSTANCE_CHANGE_EMAIL_PASSWORD
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showActionSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNormalSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class ChangeEmailFragment : Fragment(R.layout.fragment_change_email) {

    private var _binding: FragmentChangeEmailBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: ChangeEmailViewModel

    private lateinit var newEmail: String

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeEmailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[ChangeEmailViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        toolbarNavigationBackOnClick()
        subscribeToObservers()
        changeEmailOnClick()
        changeEmailActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val password = binding.tiEditPassword.text.toString().trim()
            val newEmail = binding.tiEditNewEmail.text.toString().trim().lowercase(Locale.US)

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

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        }
    }

    private fun textInputsBackgroundColor() = binding.apply {
        tiEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditNewEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutNewEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutNewEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditPassword.addTextChangedListener {
            tiLayoutPassword.isErrorEnabled = false
            tiLayoutPassword.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }

        tiEditNewEmail.addTextChangedListener {
            tiLayoutNewEmail.isErrorEnabled = false
            tiLayoutNewEmail.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
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
        changeUserEmailObserver()
    }

    private fun changeEmailOnClick() = binding.btnChangeEmail.setOnClickListener {
        validateChangeUserEmailInputs()
    }

    private fun changeEmailActionDone() =
        binding.tiEditNewEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateChangeUserEmailInputs()
            }
            false
        }

    private fun validateChangeUserEmailInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            val password = binding.tiEditPassword.text.toString().trim()
            newEmail = binding.tiEditNewEmail.text.toString().trim().lowercase(Locale.US)

            viewModel.validateChangeEmailInputs(password, newEmail)
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { validateChangeUserEmailInputs() }
            Timber.e("validateChangeUserEmailInputs Error: Offline")
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
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.change_email_error_blank_password)
                                    binding.tiLayoutPassword.requestFocus()
                                }
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutNewEmail.error =
                                        requireContext().getString(R.string.change_email_error_blank_new_email)
                                    binding.tiLayoutNewEmail.requestFocus()
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutNewEmail.requestFocus()
                                    binding.tiLayoutNewEmail.error =
                                        requireContext().getString(R.string.change_email_error_new_email)
                                }
                                it.contains(ERROR_INPUT_EMAIL_SAME) -> {
                                    binding.tiLayoutNewEmail.requestFocus()
                                    binding.tiLayoutNewEmail.error =
                                        requireContext().getString(R.string.change_email_error_email_same)
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
                    is Resource.Success -> changeUserEmail()
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
                                        requireContext(), requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_AUTH_CREDENTIALS) -> {
                                    snackbar = showNormalSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.change_email_error_credentials)
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

    private fun changeUserEmail() = viewModel.changeUserEmail(newEmail)

    private fun changeUserEmailObserver() =
        viewModel.changeUserEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        showOneBtnAlertDialog(
                            requireContext(),
                            R.string.change_email_dialog_message,
                            R.string.change_email_dialog_positive,
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
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS) -> {
                                    showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.change_email_error_email_exists),
                                        requireContext().getString(R.string.error_btn_confirm)
                                    ) { snackbar?.dismiss() }
                                }
                                else -> showSomethingWentWrongError(requireContext(), requireView())
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        tiEditPassword.isEnabled = false
        tiEditNewEmail.isEnabled = false
        btnChangeEmail.isClickable = false
        btnChangeEmail.text = null
        cpiChangeEmail.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiChangeEmail.visibility = GONE
        tiEditPassword.isEnabled = true
        tiEditNewEmail.isEnabled = true
        btnChangeEmail.isClickable = true
        btnChangeEmail.text = requireContext().getString(R.string.change_email_btn_change)
    }
}