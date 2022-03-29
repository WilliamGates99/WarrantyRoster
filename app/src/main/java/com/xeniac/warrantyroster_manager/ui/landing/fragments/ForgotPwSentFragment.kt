package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.models.Resource
import com.xeniac.warrantyroster_manager.ui.landing.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPwSentFragment : Fragment(R.layout.fragment_forgot_pw_sent) {

    private var _binding: FragmentForgotPwSentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LandingViewModel

    private lateinit var email: String

    @Inject
    lateinit var decimalFormat: DecimalFormat

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwSentBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        getEmailFromArgs()
        returnOnClick()
        resendOnClick()
        subscribeToObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelCountdown()
        snackbar?.dismiss()
        _binding = null
    }

    private fun getEmailFromArgs() {
        val args: ForgotPwSentFragmentArgs by navArgs()
        email = args.email
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        requireActivity().onBackPressed()
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
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        binding.lavSent.playAnimation()
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_connection),
                                        LENGTH_LONG
                                    ).apply {
                                        setAction(requireContext().getString(R.string.network_error_retry)) { resendResetPasswordEmail() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_TIMER_IS_NOT_ZERO) -> {
                                    val seconds = viewModel.timerInMillis / 1000
                                    if (seconds <= 1L) {
                                        snackbar = Snackbar.make(
                                            binding.root,
                                            requireContext().getString(
                                                R.string.forgot_pw_error_timer_is_not_zero_one,
                                                seconds
                                            ),
                                            LENGTH_LONG
                                        ).apply {
                                            show()
                                        }
                                    } else {
                                        snackbar = Snackbar.make(
                                            binding.root,
                                            requireContext().getString(
                                                R.string.forgot_pw_error_timer_is_not_zero_other,
                                                seconds
                                            ),
                                            LENGTH_LONG
                                        ).apply {
                                            show()
                                        }
                                    }
                                }
                                it.contains(ERROR_NETWORK_403) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_403),
                                        LENGTH_LONG
                                    ).apply {
                                        show()
                                    }
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.firebase_error_device_blocked),
                                        LENGTH_LONG
                                    ).apply {
                                        show()
                                    }
                                }
                                else -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_failure),
                                        LENGTH_LONG
                                    ).apply {
                                        show()
                                    }
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
                when (millisUntilFinished) {
                    0L -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            resetConstraintToDefault()
                            binding.groupTimer.visibility = GONE
                            binding.groupResend.visibility = VISIBLE
                        }
                    }
                    else -> {
                        binding.tvResent.text = if (viewModel.isFirstSentEmail)
                            requireContext().getString(R.string.forgot_pw_sent_text_first_time)
                        else
                            requireContext().getString(R.string.forgot_pw_sent_text_resent)

                        updateConstraintToTimer()
                        binding.groupResend.visibility = GONE
                        binding.groupTimer.visibility = VISIBLE

                        val minutes = millisUntilFinished / 60000
                        val seconds = (millisUntilFinished / 1000) % 60

                        val time =
                            "(${decimalFormat.format(minutes)}:${decimalFormat.format(seconds)})"
                        binding.tvTimer.text = time
                    }
                }
            }
        }

    private fun showLoadingAnimation() {
        binding.btnResend.visibility = GONE
        binding.cpiResend.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiResend.visibility = GONE
        binding.btnResend.visibility = VISIBLE
    }

    private fun updateConstraintToTimer() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.cl)
        constraintSet.connect(
            binding.lavSent.id,
            ConstraintSet.BOTTOM,
            binding.tvResent.id,
            ConstraintSet.TOP
        )
        constraintSet.applyTo(binding.cl)
    }

    private fun resetConstraintToDefault() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.cl)
        constraintSet.connect(
            binding.lavSent.id,
            ConstraintSet.BOTTOM,
            binding.tvResend.id,
            ConstraintSet.TOP
        )
        constraintSet.applyTo(binding.cl)
    }
}