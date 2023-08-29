package com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.core.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwSentBinding
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNormalSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPwSentFragment @Inject constructor(
    private val decimalFormat: DecimalFormat,
    var viewModel: ForgotPwViewModel? = null
) : Fragment(R.layout.fragment_forgot_pw_sent) {

    private var _binding: FragmentForgotPwSentBinding? = null
    val binding get() = _binding!!

    private lateinit var email: String
    private var isFirstTimeSendingEmail = true
    var timerMillisUntilFinished = 0L

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwSentBinding.bind(view)
        viewModel = viewModel ?: ViewModelProvider(requireActivity())[ForgotPwViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        getEmailFromArgs()
        subscribeToObservers()
        returnOnClick()
        resendOnClick()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
        _binding = null
        super.onDestroyView()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popBackStack()
        }
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        } else {
            networkStatus = ConnectivityObserver.Status.AVAILABLE
        }
    }

    private fun getEmailFromArgs() {
        val args: ForgotPwSentFragmentArgs by navArgs()
        email = args.email
    }

    private fun subscribeToObservers() {
        resendResetPasswordEmailObserver()
        isNotFirstTimeSendingEmailObserver()
        timerMillisUntilFinishedObserver()
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        popBackStack()
    }

    private fun resendOnClick() = binding.btnResend.setOnClickListener {
        resendResetPasswordEmail()
    }

    private fun resendResetPasswordEmail() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel!!.sendResetPasswordEmail(email)
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { resendResetPasswordEmail() }
            Timber.e("resendResetPasswordEmail Error: Offline")
        }
    }

    private fun resendResetPasswordEmailObserver() =
        viewModel!!.sendResetPasswordEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                it.contains(ERROR_TIMER_IS_NOT_ZERO) -> {
                                    val seconds = (timerMillisUntilFinished / 1000).toInt()
                                    val message = requireContext().resources.getQuantityString(
                                        R.plurals.forgot_pw_error_timer_is_not_zero,
                                        seconds,
                                        seconds
                                    )
                                    showNormalSnackbarError(requireView(), message)
                                }
                                else -> showSomethingWentWrongError(requireContext(), requireView())
                            }
                        }
                    }
                }
            }
        }

    private fun isNotFirstTimeSendingEmailObserver() =
        viewModel!!.isNotFirstTimeSendingEmailLiveData.observe(viewLifecycleOwner) { isNotFirstTime ->
            isFirstTimeSendingEmail = !isNotFirstTime
        }

    private fun timerMillisUntilFinishedObserver() =
        viewModel!!.timerMillisUntilFinishedLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { millisUntilFinished ->
                timerMillisUntilFinished = millisUntilFinished
                binding.apply {
                    when (millisUntilFinished) {
                        0L -> hideTimer()
                        else -> showTimer(millisUntilFinished)
                    }
                }
            }
        }

    private fun showTimer(millisUntilFinished: Long) = binding.apply {
        setResendTvConstraintBottomToTopOfTimerTv()

        resendText = if (isFirstTimeSendingEmail) {
            requireContext().getString(R.string.forgot_pw_sent_text_first_time)
        } else {
            requireContext().getString(R.string.forgot_pw_sent_text_resent)
        }

        val minutes = decimalFormat.format(millisUntilFinished / 60000)
        val seconds = decimalFormat.format((millisUntilFinished / 1000) % 60)

        time = requireContext().getString(
            R.string.forgot_pw_sent_text_timer,
            minutes,
            seconds
        )
        isTimerTicking = true
    }

    private fun setResendTvConstraintBottomToTopOfTimerTv() = ConstraintSet().apply {
        clone(binding.cl)
        connect(
            /* startID = */ binding.tvResend.id,
            /* startSide = */ ConstraintSet.BOTTOM,
            /* endID = */ binding.tvTimer.id,
            /* endSide = */ ConstraintSet.TOP
        )
        applyTo(binding.cl)
    }

    private fun hideTimer() = CoroutineScope(Dispatchers.Main).launch {
        delay(500)
        binding.apply {
            setResendTvConstraintBottomToTopOfResendBtn()
            resendText = requireContext().getString(R.string.forgot_pw_sent_text_resend)
            isTimerTicking = false
        }
    }

    private fun setResendTvConstraintBottomToTopOfResendBtn() = ConstraintSet().apply {
        clone(binding.cl)
        connect(
            /* startID = */ binding.tvResend.id,
            /* startSide = */ ConstraintSet.BOTTOM,
            /* endID = */ binding.btnResend.id,
            /* endSide = */ ConstraintSet.TOP
        )
        applyTo(binding.cl)
    }

    private fun showLoadingAnimation() {
        binding.isResendLoading = true
    }

    private fun hideLoadingAnimation() {
        binding.isResendLoading = false
    }
}