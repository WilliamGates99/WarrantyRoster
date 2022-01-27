package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.DecimalFormat

class ForgotPwSentFragment : Fragment(R.layout.fragment_forgot_pw_sent) {

    private var _binding: FragmentForgotPwSentBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: String
    private var countDownTimer: CountDownTimer? = null

    private val TAG = "ForgotPwSentFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwSentBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()

        getEmailFromArgs()
        returnOnClick()
        resendOnClick()
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

    private fun resendResetPasswordEmail() {
        if (hasInternetConnection(requireContext())) {
            resendResetPasswordEmailAuth()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { resendResetPasswordEmail() }
                show()
            }
        }
    }

    private fun resendResetPasswordEmailAuth() {
        showLoadingAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Log.i(TAG, "Reset password email successfully sent to ${email}.")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    countdown()
                    binding.lavSent.playAnimation()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    Snackbar.make(
                        binding.root,
                        requireContext().getString(R.string.network_error_failure),
                        LENGTH_LONG
                    ).show()
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