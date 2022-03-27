package com.xeniac.warrantyroster_manager.ui.landing

import android.app.Application
import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.di.LoginPrefs
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.models.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.models.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    @LoginPrefs private val loginPrefs: SharedPreferences
) : AndroidViewModel(application) {

    private val _registerLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val registerLiveData: LiveData<Event<Resource<Nothing>>> = _registerLiveData

    private val _loginLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginLiveData: LiveData<Event<Resource<Nothing>>> = _loginLiveData

    private val _forgotPwLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val forgotPwLiveData: LiveData<Event<Resource<String>>> = _forgotPwLiveData

    private val _timerLiveData: MutableLiveData<Event<Long>> = MutableLiveData()
    val timerLiveData: LiveData<Event<Long>> = _timerLiveData

    private lateinit var countDownTimer: CountDownTimer
    var isFirstSentEmail = true
    var timerInMillis: Long = 0

    fun registerViaEmail(email: String, password: String) = viewModelScope.launch {
        safeRegisterViaEmail(email, password)
    }

    fun loginViaEmail(email: String, password: String) = viewModelScope.launch {
        safeLoginViaEmail(email, password)
    }

    fun sendResetPasswordEmail(email: String) = viewModelScope.launch {
        safeSendResetPasswordEmail(email)
    }

    private suspend fun safeRegisterViaEmail(email: String, password: String) {
        _registerLiveData.postValue(Event(Resource.Loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.registerViaEmail(email, password).await()
                userRepository.sendVerificationEmail()
                loginPrefs.edit().putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true).apply()
                _registerLiveData.postValue(Event(Resource.Success(null)))
                Timber.i("$email registered successfully.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _registerLiveData.postValue(
                    Event(Resource.Error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("safeRegisterViaEmail Exception: ${e.message}")
            _registerLiveData.postValue(Event(Resource.Error(e.message.toString())))
        }
    }

    private suspend fun safeLoginViaEmail(email: String, password: String) {
        _loginLiveData.postValue(Event(Resource.Loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.loginViaEmail(email, password).await().apply {
                    user?.let {
                        loginPrefs.edit().putBoolean(PREFERENCE_IS_LOGGED_IN_KEY, true).apply()
                        _loginLiveData.postValue(Event(Resource.Success(null)))
                        Timber.i("$email logged in successfully.")
                    }
                }
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _loginLiveData.postValue(
                    Event(Resource.Error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("safeLoginViaEmail Exception: ${e.message}")
            _loginLiveData.postValue(Event(Resource.Error(e.message.toString())))
        }
    }

    private suspend fun safeSendResetPasswordEmail(email: String) {
        _forgotPwLiveData.postValue(Event(Resource.Loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                when (timerInMillis) {
                    0L -> {
                        userRepository.sendResetPasswordEmail(email).await().apply {
                            _forgotPwLiveData.postValue(Event(Resource.Success(email)))
                            startCountdown()
                            Timber.i("Reset password email successfully sent to ${email}.")
                        }
                    }
                    else -> {
                        Timber.e(ERROR_TIMER_IS_NOT_ZERO)
                        _forgotPwLiveData.postValue(Event(Resource.Error(ERROR_TIMER_IS_NOT_ZERO)))
                    }
                }
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _forgotPwLiveData.postValue(Event(Resource.Error(ERROR_NETWORK_CONNECTION)))
            }
        } catch (e: Exception) {
            Timber.e("safeSendResetPasswordEmail Exception: ${e.message}")
            _forgotPwLiveData.postValue(Event(Resource.Error(e.message.toString())))
        }
    }

    private fun startCountdown() {
        val startTimeInMillis = 120 * 1000L // 120 Seconds
        val countDownIntervalInMillis = 1000L // 1 Second

        countDownTimer = object : CountDownTimer(startTimeInMillis, countDownIntervalInMillis) {
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

    fun cancelCountdown() = countDownTimer.cancel()
}