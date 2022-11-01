package com.xeniac.warrantyroster_manager.ui.viewmodels

import android.os.Build
import android.os.CountDownTimer
import android.util.LayoutDirection
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.text.layoutDirection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.UserHelper.isEmailValid
import com.xeniac.warrantyroster_manager.utils.UserHelper.isRetypePasswordValid
import com.xeniac.warrantyroster_manager.utils.UserHelper.passwordStrength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentLocaleIndexLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val currentLocaleIndexLiveData: LiveData<Event<Int>> = _currentLocaleIndexLiveData

    private val _changeCurrentLocaleLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val changeCurrentLocaleLiveData: LiveData<Event<Boolean>> = _changeCurrentLocaleLiveData

    private val _registerLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val registerLiveData: LiveData<Event<Resource<Nothing>>> = _registerLiveData

    private val _forgotPwLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val forgotPwLiveData: LiveData<Event<Resource<String>>> = _forgotPwLiveData

    private val _timerLiveData: MutableLiveData<Event<Long>> = MutableLiveData()
    val timerLiveData: LiveData<Event<Long>> = _timerLiveData

    var forgotPwEmail: String? = null
    var isFirstSentEmail = true
    var timerInMillis: Long = 0

    fun isUserLoggedIn() = preferencesRepository.isUserLoggedInSynchronously()

    fun getCurrentLanguage() = viewModelScope.launch {
        safeGetCurrentLanguage()
    }

    private fun safeGetCurrentLanguage() {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            changeCurrentLocale(0)
            Timber.i("Locale list is Empty.")
        } else {
            val localeString = localeList[0].toString()
            Timber.i("Current language is $localeString")

            when (localeString) {
                "en_US" -> {
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_ENGLISH_UNITED_STATES))
                    Timber.i("Current locale index is 0 (en_US).")
                }
                "en_GB" -> {
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_ENGLISH_GREAT_BRITAIN))
                    Timber.i("Current locale index is 1 (en_GB).")
                }
                "fa_IR" -> {
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_PERSIAN_IRAN))
                    Timber.i("Current locale index is 2 (fa_IR).")
                }
                else -> {
                    changeCurrentLocale(0)
                    Timber.i("Current language is System Default.")
                }
            }
        }
    }

    fun changeCurrentLocale(index: Int) = viewModelScope.launch {
        safeChangeCurrentLocale(index)
    }

    private suspend fun safeChangeCurrentLocale(index: Int) {
        var isActivityRestartNeeded = false

        when (index) {
            0 -> {
                preferencesRepository.setCategoryTitleMapKey(LOCALE_ENGLISH_UNITED_STATES)
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.LTR)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_ENGLISH_UNITED_STATES)
                )
            }
            1 -> {
                preferencesRepository.setCategoryTitleMapKey(LOCALE_ENGLISH_GREAT_BRITAIN)
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.LTR)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_ENGLISH_GREAT_BRITAIN)
                )
            }
            2 -> {
                preferencesRepository.setCategoryTitleMapKey(LOCALE_PERSIAN_IRAN)
                isActivityRestartNeeded = isActivityRestartNeeded(LayoutDirection.RTL)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(LOCALE_PERSIAN_IRAN)
                )
            }
        }

        _changeCurrentLocaleLiveData.postValue(Event(isActivityRestartNeeded))
        Timber.i("isActivityRestartNeeded = $isActivityRestartNeeded}")
        Timber.i("App locale index changed to $index")
    }

    fun checkRegisterInputs(email: String, password: String, retypePassword: String) {
        if (email.isBlank()) {
            _registerLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_EMAIL)))
            return
        }

        if (password.isBlank()) {
            _registerLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_PASSWORD)))
            return
        }

        if (retypePassword.isBlank()) {
            _registerLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_RETYPE_PASSWORD)))
            return
        }

        if (!isEmailValid(email)) {
            _registerLiveData.postValue(Event(Resource.error(ERROR_INPUT_EMAIL_INVALID)))
            return
        }

        if (passwordStrength(password) == (-1).toByte()) {
            _registerLiveData.postValue(Event(Resource.error(ERROR_INPUT_PASSWORD_SHORT)))
            return
        }

        if (!isRetypePasswordValid(password, retypePassword)) {
            _registerLiveData.postValue(Event(Resource.error(ERROR_INPUT_PASSWORD_NOT_MATCH)))
            return
        }

        registerViaEmail(email, password)
    }

    fun checkForgotPwInputs(email: String, activateCountDown: Boolean = true) {
        if (email.isBlank()) {
            _forgotPwLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_EMAIL)))
            return
        }

        if (!isEmailValid(email)) {
            _forgotPwLiveData.postValue(Event(Resource.error(ERROR_INPUT_EMAIL_INVALID)))
            return
        }

        sendResetPasswordEmail(email, activateCountDown)
    }

    fun registerViaEmail(email: String, password: String) = viewModelScope.launch {
        safeRegisterViaEmail(email, password)
    }

    fun sendResetPasswordEmail(email: String, activateCountDown: Boolean = true) =
        viewModelScope.launch {
            safeSendResetPasswordEmail(email, activateCountDown)
        }

    private suspend fun safeRegisterViaEmail(email: String, password: String) {
        _registerLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.registerViaEmail(email, password)
            userRepository.sendVerificationEmail()
            preferencesRepository.isUserLoggedIn(true)
            _registerLiveData.postValue(Event(Resource.success(null)))
            Timber.i("$email registered successfully.")
        } catch (e: Exception) {
            Timber.e("safeRegisterViaEmail Exception: ${e.message}")
            _registerLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeSendResetPasswordEmail(email: String, activateCountDown: Boolean) {
        _forgotPwLiveData.postValue(Event(Resource.loading()))
        try {
            if (email == forgotPwEmail && timerInMillis != 0L) {
                Timber.e(ERROR_TIMER_IS_NOT_ZERO)
                _forgotPwLiveData.postValue(Event(Resource.error(ERROR_TIMER_IS_NOT_ZERO)))
            } else {
                userRepository.sendResetPasswordEmail(email)
                _forgotPwLiveData.postValue(Event(Resource.success(email)))
                forgotPwEmail = email
                if (activateCountDown) startCountdown()
                Timber.i("Reset password email successfully sent to ${email}.")
            }
        } catch (e: Exception) {
            Timber.e("safeSendResetPasswordEmail Exception: ${e.message}")
            _forgotPwLiveData.postValue(Event(Resource.error(e.message.toString())))
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

    private fun isActivityRestartNeeded(newLayoutDirection: Int): Boolean {
        val currentLocale = AppCompatDelegate.getApplicationLocales()[0]
        val currentLayoutDirection = currentLocale?.layoutDirection

        return if (Build.VERSION.SDK_INT >= 33) {
            false
        } else currentLayoutDirection != newLayoutDirection
    }
}