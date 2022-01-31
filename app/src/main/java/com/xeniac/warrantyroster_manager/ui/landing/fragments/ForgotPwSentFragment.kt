package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.ui.landing.LandingActivity
import com.xeniac.warrantyroster_manager.ui.landing.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Resource
import java.text.DecimalFormat

class ForgotPwSentFragment : Fragment(R.layout.fragment_forgot_pw_sent) {

    private var _binding: FragmentForgotPwSentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LandingViewModel

    private lateinit var email: String
    private var countDownTimer: CountDownTimer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwSentBinding.bind(view)
        viewModel = (activity as LandingActivity).viewModel

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
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> showLoadingAnimation()
                is Resource.Success -> {
                    hideLoadingAnimation()
                    countdown()
                    binding.lavSent.playAnimation()
                }
                is Resource.Error -> {
                    hideLoadingAnimation()
                    response.message?.let {
                        when {
                            it.contains("Unable to connect to the internet") -> {
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

            if (viewModel.forgotPwLiveData.value != null) {
                viewModel.forgotPwLiveData.value = null
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
                    binding.groupTimer.visibility = GONE
                    binding.groupResend.visibility = VISIBLE
                }, 500)
            }
        }.start()
    }
}