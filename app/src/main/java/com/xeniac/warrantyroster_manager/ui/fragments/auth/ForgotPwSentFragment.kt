package com.xeniac.warrantyroster_manager.ui.fragments.auth

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.ForgotPwViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNormalSnackbarError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPwSentFragment : Fragment(R.layout.fragment_forgot_pw_sent) {

    private var _binding: FragmentForgotPwSentBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: ForgotPwViewModel

    private lateinit var email: String

    @Inject
    lateinit var decimalFormat: DecimalFormat

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwSentBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[ForgotPwViewModel::class.java]

        getEmailFromArgs()
        subscribeToObservers()
        returnOnClick()
        resendOnClick()
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

    private fun getEmailFromArgs() {
        val args: ForgotPwSentFragmentArgs by navArgs()
        email = args.email
    }

    private fun subscribeToObservers() {
        forgotPwSentObserver()
        timerObserver()
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        popBackStack()
    }

    private fun resendOnClick() = binding.btnResend.setOnClickListener {
        resendResetPasswordEmail()
    }

    private fun resendResetPasswordEmail() = viewModel.sendResetPasswordEmail(email)

    private fun forgotPwSentObserver() =
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        binding.lavSent.playAnimation()
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { resendResetPasswordEmail() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                it.contains(ERROR_TIMER_IS_NOT_ZERO) -> {
                                    val seconds = (viewModel.timerInMillis / 1000).toInt()
                                    val message = requireContext().resources.getQuantityString(
                                        R.plurals.forgot_pw_error_timer_is_not_zero,
                                        seconds,
                                        seconds
                                    )
                                    showNormalSnackbarError(requireView(), message)
                                }
                                else -> showNetworkFailureError(requireContext(), requireView())
                            }
                        }
                    }
                }
            }
        }

    private fun timerObserver() =
        viewModel.timerLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { millisUntilFinished ->
                binding.apply {
                    when (millisUntilFinished) {
                        0L -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                updateTimerViews(false)
                            }
                        }
                        else -> {
                            updateTimerViews(true)

                            tvResend.text = if (viewModel.isFirstSentEmail)
                                requireContext().getString(R.string.forgot_pw_sent_text_first_time)
                            else requireContext().getString(R.string.forgot_pw_sent_text_resent)

                            val minutes = decimalFormat.format(millisUntilFinished / 60000)
                            val seconds = decimalFormat.format((millisUntilFinished / 1000) % 60)
                            time = "($minutes:$seconds})"
                        }
                    }
                }
            }
        }

    private fun updateTimerViews(isTimerActive: Boolean) {
        binding.isTimerTicking = isTimerActive
        setResendTvConstraintBottom(isTimerActive)
    }

    private fun setResendTvConstraintBottom(isTimerActive: Boolean) = binding.apply {
        val constraintEndId = if (isTimerActive) tvTimer.id else btnResend.id

        ConstraintSet().apply {
            clone(cl)
            connect(
                /* startID = */ tvResend.id,
                /* startSide = */ ConstraintSet.BOTTOM,
                /* endID = */ constraintEndId,
                /* endSide = */ ConstraintSet.TOP
            )
            applyTo(cl)
        }
    }

    private fun showLoadingAnimation() {
        binding.isResendLoading = true
    }

    private fun hideLoadingAnimation() {
        binding.isResendLoading = false
    }
}