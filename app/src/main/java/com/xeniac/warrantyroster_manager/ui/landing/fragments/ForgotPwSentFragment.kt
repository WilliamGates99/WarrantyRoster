package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import com.xeniac.warrantyroster_manager.models.Status
import com.xeniac.warrantyroster_manager.ui.landing.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class ForgotPwSentFragment : Fragment(R.layout.fragment_forgot_pw_sent) {

    private var _binding: FragmentForgotPwSentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LandingViewModel

    private lateinit var email: String
    private var countDownTimer: CountDownTimer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwSentBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        getEmailFromArgs()
        returnOnClick()
        resendOnClick()
        forgotPwSentObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
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

    private fun resendResetPasswordEmail() = viewModel.sendResetPasswordEmail(email)

    private fun forgotPwSentObserver() =
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                showLoadingAnimation()
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        countdown()
                        binding.lavSent.playAnimation()
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_connection),
                                        LENGTH_LONG
                                    ).apply {
                                        setAction(requireContext().getString(R.string.network_error_retry)) { resendResetPasswordEmail() }
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
        binding.btnResend.visibility = GONE
        binding.cpiResend.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiResend.visibility = GONE
        binding.btnResend.visibility = VISIBLE
    }

    private fun countdown() {
        updateConstraintToTimer()
        binding.groupResend.visibility = GONE
        binding.groupTimer.visibility = VISIBLE

        val decimalFormat = DecimalFormat("00")
        val startTime = 120 * 1000

        countDownTimer = object : CountDownTimer(startTime.toLong(), 1) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished / 1000) % 60

                val time = "(${decimalFormat.format(minutes)}:${decimalFormat.format(seconds)})"
                binding.tvTimer.text = time
            }

            override fun onFinish() {
                Handler(Looper.getMainLooper()).postDelayed({
                    resetConstraintToDefault()
                    binding.groupTimer.visibility = GONE
                    binding.groupResend.visibility = VISIBLE
                }, 500)
            }
        }.start()
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