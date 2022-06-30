package com.xeniac.warrantyroster_manager.ui.landing.fragments

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
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_FORGOT_PW_EMAIL
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthAccountNotFoundError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showTimerIsNotZeroError
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPwFragment : Fragment(R.layout.fragment_forgot_pw) {

    private var _binding: FragmentForgotPwBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: LandingViewModel

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        onBackPressed()
        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnOnClick()
        sendOnClick()
        sendActionDone()
        forgotPwObserver()
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
            val email = binding.tiEditEmail.text.toString().trim().lowercase()

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

    private fun textInputsStrokeColor() = binding.tiEditEmail.addTextChangedListener {
        binding.tiLayoutEmail.isErrorEnabled = false
        binding.tiLayoutEmail.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.blue)
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun sendOnClick() = binding.btnSend.setOnClickListener {
        getResetPasswordInput()
    }

    private fun sendActionDone() =
        binding.tiEditEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getResetPasswordInput()
            }
            false
        }

    private fun getResetPasswordInput() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase()

        viewModel.checkForgotPwInputs(email)
    }

    private fun forgotPwObserver() =
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        response.data?.let { email ->
                            navigateToForgotPwSent(email)
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.forgot_pw_error_email)
                                }
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), binding.root
                                    ) { getResetPasswordInput() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(),
                                        binding.root
                                    )
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND) -> {
                                    snackbar = showFirebaseAuthAccountNotFoundError(
                                        binding.root,
                                        requireContext().getString(R.string.forgot_pw_error_not_found),
                                        requireContext().getString(R.string.error_btn_confirm)
                                    ) { snackbar?.dismiss() }
                                }
                                it.contains(ERROR_TIMER_IS_NOT_ZERO) -> {
                                    val seconds = viewModel.timerInMillis / 1000
                                    snackbar = showTimerIsNotZeroError(
                                        requireContext(),
                                        binding.root,
                                        seconds
                                    )
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(),
                                        binding.root
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
}