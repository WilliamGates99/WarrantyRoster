package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showTimerIsNotZeroError
import com.xeniac.warrantyroster_manager.utils.Status
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
        returnOnClick()
        resendOnClick()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private fun getEmailFromArgs() {
        val args: ForgotPwSentFragmentArgs by navArgs()
        email = args.email
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun resendOnClick() = binding.btnResend.setOnClickListener {
        resendResetPasswordEmail()
    }

    private fun subscribeToObservers() {
        forgotPwSentObserver()
        timerObserver()
    }

    private fun resendResetPasswordEmail() = viewModel.sendResetPasswordEmail(email)

    private fun forgotPwSentObserver() =
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                showLoadingAnimation()
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        binding.lavSent.playAnimation()
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
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
                                    showTimerIsNotZeroError(
                                        requireContext(),
                                        requireView(),
                                        seconds
                                    )
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

    private fun timerObserver() =
        viewModel.timerLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { millisUntilFinished ->
                binding.apply {
                    when (millisUntilFinished) {
                        0L -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                resetConstraintToDefault()
                                groupTimer.visibility = GONE
                                groupResend.visibility = VISIBLE
                            }
                        }
                        else -> {
                            tvResent.text = if (viewModel.isFirstSentEmail)
                                requireContext().getString(R.string.forgot_pw_sent_text_first_time)
                            else
                                requireContext().getString(R.string.forgot_pw_sent_text_resent)

                            updateConstraintToTimer()
                            groupResend.visibility = GONE
                            groupTimer.visibility = VISIBLE

                            val minutes = millisUntilFinished / 60000
                            val seconds = (millisUntilFinished / 1000) % 60

                            val time =
                                "(${decimalFormat.format(minutes)}:${decimalFormat.format(seconds)})"
                            tvTimer.text = time
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        btnResend.visibility = GONE
        cpiResend.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiResend.visibility = GONE
        btnResend.visibility = VISIBLE
    }

    private fun updateConstraintToTimer() = binding.apply {
        val constraintSet = ConstraintSet()
        constraintSet.clone(cl)
        constraintSet.connect(
            lavSent.id,
            ConstraintSet.BOTTOM,
            tvResent.id,
            ConstraintSet.TOP
        )
        constraintSet.applyTo(cl)
    }

    private fun resetConstraintToDefault() = binding.apply {
        val constraintSet = ConstraintSet()
        constraintSet.clone(cl)
        constraintSet.connect(
            lavSent.id,
            ConstraintSet.BOTTOM,
            tvResend.id,
            ConstraintSet.TOP
        )
        constraintSet.applyTo(cl)
    }
}