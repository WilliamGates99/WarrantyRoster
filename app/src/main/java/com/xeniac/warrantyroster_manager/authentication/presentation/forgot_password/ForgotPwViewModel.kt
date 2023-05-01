package com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.util.Constants.TIMER_COUNTDOWN_INTERVAL_IN_MILLIS
import com.xeniac.warrantyroster_manager.util.Constants.TIMER_START_TIME_IN_MILLIS
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.util.UserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForgotPwViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var previousSentEmail: String? = null
    private var countDownTimer: CountDownTimer? = null

    private val _sendResetPasswordEmailLiveData: MutableLiveData<Event<Resource<String>>> =
        MutableLiveData()
    val sendResetPasswordEmailLiveData: LiveData<Event<Resource<String>>> =
        _sendResetPasswordEmailLiveData

    private val _timerMillisUntilFinishedLiveData: MutableLiveData<Event<Long>> = MutableLiveData()
    val timerMillisUntilFinishedLiveData: LiveData<Event<Long>> = _timerMillisUntilFinishedLiveData

    private val _isNotFirstTimeSendingEmailLiveData: MutableLiveData<Boolean> =
        savedStateHandle.getLiveData("isNotFirstTimeSendingEmail", false)
    val isNotFirstTimeSendingEmailLiveData: LiveData<Boolean> = _isNotFirstTimeSendingEmailLiveData

    fun validateSendResetPasswordEmail(
        email: String, activateCountDown: Boolean = true
    ) = viewModelScope.launch {
        safeValidateSendResetPasswordEmailInputs(email, activateCountDown)
    }

    private fun safeValidateSendResetPasswordEmailInputs(
        email: String,
        activateCountDown: Boolean = true
    ) {
        if (email.isBlank()) {
            _sendResetPasswordEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EMAIL)))
            )
            return
        }

        if (!UserHelper.isEmailValid(email)) {
            _sendResetPasswordEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_EMAIL_INVALID)))
            )
            return
        }

        sendResetPasswordEmail(email, activateCountDown)
    }

    fun sendResetPasswordEmail(email: String, activateCountDown: Boolean = true) =
        viewModelScope.launch {
            safeSendResetPasswordEmail(email, activateCountDown)
        }

    private suspend fun safeSendResetPasswordEmail(email: String, activateCountDown: Boolean) {
        _sendResetPasswordEmailLiveData.postValue(Event(Resource.Loading()))
        try {
            val isNotFirstTimeSendingEmail = email == previousSentEmail

            val isTimerNotZero = timerMillisUntilFinishedLiveData.value?.let {
                it.peekContent() != 0L
            } ?: false

            val shouldShowTimerIsNotZeroError = isNotFirstTimeSendingEmail && isTimerNotZero

            if (shouldShowTimerIsNotZeroError) {
                _sendResetPasswordEmailLiveData.postValue(
                    Event(Resource.Error(UiText.DynamicString(ERROR_TIMER_IS_NOT_ZERO)))
                )
                Timber.e(ERROR_TIMER_IS_NOT_ZERO)
            } else {
                userRepository.sendResetPasswordEmail(email)
                _sendResetPasswordEmailLiveData.postValue(Event(Resource.Success(email)))
                previousSentEmail = email

                if (activateCountDown) {
                    startCountdown()
                }

                savedStateHandle["isNotFirstTimeSendingEmail"] = isNotFirstTimeSendingEmail

                Timber.i("Reset password email successfully sent to ${email}.")
            }
        } catch (e: Exception) {
            Timber.e("safeSendResetPasswordEmail Exception: ${e.message}")
            _sendResetPasswordEmailLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    private fun startCountdown() {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(
            TIMER_START_TIME_IN_MILLIS,
            TIMER_COUNTDOWN_INTERVAL_IN_MILLIS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _timerMillisUntilFinishedLiveData.postValue(Event(millisUntilFinished))
                Timber.i(": ${millisUntilFinished / 1000} seconds until timer is finished.")
            }

            override fun onFinish() {
                _timerMillisUntilFinishedLiveData.postValue(Event(0L))
                _isNotFirstTimeSendingEmailLiveData.postValue(
                    savedStateHandle["isFirstTimeSendingEmail"] ?: false
                )

                Timber.i("Timer is finished.")
            }
        }.start()
    }
}