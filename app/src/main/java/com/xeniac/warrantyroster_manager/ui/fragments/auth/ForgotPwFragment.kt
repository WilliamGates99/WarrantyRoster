package com.xeniac.warrantyroster_manager.ui.fragments.auth

import android.content.Context
import android.os.Build
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.ui.viewmodels.ForgotPwViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_FORGOT_PW_EMAIL
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showActionSnackbarError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNormalSnackbarError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class ForgotPwFragment : Fragment(R.layout.fragment_forgot_pw) {

    private var _binding: FragmentForgotPwBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: ForgotPwViewModel

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[ForgotPwViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        subscribeToObservers()
        returnOnClick()
        sendOnClick()
        sendActionDone()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)

            if (email.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_FORGOT_PW_EMAIL, email)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_FORGOT_PW_EMAIL)?.let { restoredEmail ->
                binding.tiEditEmail.setText(restoredEmail)
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
        tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditEmail.addTextChangedListener {
            tiLayoutEmail.isErrorEnabled = false
            tiLayoutEmail.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun subscribeToObservers() {
        forgotPwObserver()
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        popBackStack()
    }

    private fun sendOnClick() = binding.btnSend.setOnClickListener {
        validateResetPasswordInputs()
    }

    private fun sendActionDone() =
        binding.tiEditEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateResetPasswordInputs()
            }
            false
        }

    private fun validateResetPasswordInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            val email = binding.tiEditEmail.text.toString().trim().lowercase(Locale.US)

            viewModel.validateForgotPwInputs(email)
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { validateResetPasswordInputs() }
            Timber.e("validateResetPasswordInputs Error: Offline")
        }
    }

    private fun forgotPwObserver() =
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        response.data?.let { email ->
                            navigateToForgotPwSent(email)
                        }
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.forgot_pw_error_blank_email)
                                    binding.tiLayoutEmail.requestFocus()
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.forgot_pw_error_email)
                                }
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
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND) -> {
                                    snackbar = showActionSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.forgot_pw_error_not_found),
                                        requireContext().getString(R.string.error_btn_confirm)
                                    ) { snackbar?.dismiss() }
                                }
                                it.contains(ERROR_TIMER_IS_NOT_ZERO) -> {
                                    val seconds = (viewModel.timerInMillis / 1000).toInt()
                                    val message = requireContext().resources.getQuantityString(
                                        R.plurals.forgot_pw_error_timer_is_not_zero,
                                        seconds,
                                        seconds
                                    )
                                    snackbar = showNormalSnackbarError(requireView(), message)
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

    private fun navigateToForgotPwSent(email: String) = findNavController().navigate(
        ForgotPwFragmentDirections.actionForgotPasswordFragmentToForgotPwSentFragment(email)
    )

    private fun showLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        btnSend.isClickable = false
        btnSend.text = null
        cpiSend.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiSend.visibility = GONE
        tiEditEmail.isEnabled = true
        btnSend.isClickable = true
        btnSend.text = requireContext().getString(R.string.forgot_pw_btn_send)
    }
}