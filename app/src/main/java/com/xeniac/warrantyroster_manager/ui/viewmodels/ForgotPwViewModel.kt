package com.xeniac.warrantyroster_manager.ui.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.UiText
import com.xeniac.warrantyroster_manager.utils.UserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForgotPwViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _forgotPwLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val forgotPwLiveData: LiveData<Event<Resource<String>>> = _forgotPwLiveData

    private val _timerLiveData: MutableLiveData<Event<Long>> = MutableLiveData()
    val timerLiveData: LiveData<Event<Long>> = _timerLiveData

    var forgotPwEmail: String? = null
    var isFirstSentEmail = true
    var timerInMillis: Long = 0

    fun validateForgotPwInputs(email: String, activateCountDown: Boolean = true) {
        if (email.isBlank()) {
            _forgotPwLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EMAIL)))
            )
            return
        }

        if (!UserHelper.isEmailValid(email)) {
            _forgotPwLiveData.postValue(
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
        _forgotPwLiveData.postValue(Event(Resource.Loading()))
        try {
            if (email == forgotPwEmail && timerInMillis != 0L) {
                Timber.e(ERROR_TIMER_IS_NOT_ZERO)
                _forgotPwLiveData.postValue(
                    Event(Resource.Error(UiText.DynamicString(ERROR_TIMER_IS_NOT_ZERO)))
                )
            } else {
                userRepository.sendResetPasswordEmail(email)
                _forgotPwLiveData.postValue(Event(Resource.Success(email)))
                forgotPwEmail = email
                if (activateCountDown) startCountdown()
                Timber.i("Reset password email successfully sent to ${email}.")
            }
        } catch (e: Exception) {
            Timber.e("safeSendResetPasswordEmail Exception: ${e.message}")
            _forgotPwLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    private fun startCountdown() {
        val startTimeInMillis = 120 * 1000L // 120 Seconds
        val countDownIntervalInMillis = 1000L // 1 Second

        object : CountDownTimer(startTimeInMillis, countDownIntervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                timerInMillis = millisUntilFinished
                _timerLiveData.postValue(Event(millisUntilFinished))
                Timber.i("timer: $millisUntilFinished")
            }

            override fun onFinish() {
                isFirstSentEmail = false
                timerInMillis = 0
                _timerLiveData.postValue(Event(0))
            }
        }.start()
    }
}