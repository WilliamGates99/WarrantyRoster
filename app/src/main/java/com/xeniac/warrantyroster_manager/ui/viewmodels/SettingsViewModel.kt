package com.xeniac.warrantyroster_manager.ui.viewmodels

import android.os.Build
import android.util.LayoutDirection
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.text.layoutDirection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_SAME
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SettingsHelper
import com.xeniac.warrantyroster_manager.utils.UiText
import com.xeniac.warrantyroster_manager.utils.UserHelper.isEmailValid
import com.xeniac.warrantyroster_manager.utils.UserHelper.isRetypePasswordValid
import com.xeniac.warrantyroster_manager.utils.UserHelper.passwordStrength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentLanguageLiveData: MutableLiveData<Event<UiText>> = MutableLiveData()
    val currentLanguageLiveData: LiveData<Event<UiText>> = _currentLanguageLiveData

    private val _currentLocaleIndexLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val currentLocaleIndexLiveData: LiveData<Event<Int>> = _currentLocaleIndexLiveData

    private val _changeCurrentLocaleLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val changeCurrentLocaleLiveData: LiveData<Event<Boolean>> = _changeCurrentLocaleLiveData

    private val _currentAppThemeLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val currentAppThemeLiveData: LiveData<Event<Int>> = _currentAppThemeLiveData

    private val _rateAppDialogChoiceLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val rateAppDialogChoiceLiveData: LiveData<Event<Int>> = _rateAppDialogChoiceLiveData

    private val _previousRequestTimeInMillisLiveData:
            MutableLiveData<Event<Long>> = MutableLiveData()
    val previousRequestTimeInMillisLiveData:
            LiveData<Event<Long>> = _previousRequestTimeInMillisLiveData

    private val _setAppLocaleLiveData:
            MutableLiveData<Event<Resource<Array<String>>>> = MutableLiveData()
    val setAppLocaleLiveData: LiveData<Event<Resource<Array<String>>>> = _setAppLocaleLiveData

    private val _accountDetailsLiveData:
            MutableLiveData<Event<Resource<FirebaseUser>>> = MutableLiveData()
    val accountDetailsLiveData: LiveData<Event<Resource<FirebaseUser>>> = _accountDetailsLiveData

    private val _sendVerificationEmailLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val sendVerificationEmailLiveData:
            LiveData<Event<Resource<Nothing>>> = _sendVerificationEmailLiveData

    private val _logoutLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val logoutLiveData: LiveData<Event<Resource<Nothing>>> = _logoutLiveData

    private val _checkInputsLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val checkInputsLiveData: LiveData<Event<Resource<String>>> = _checkInputsLiveData

    private val _reAuthenticateUserLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val reAuthenticateUserLiveData: LiveData<Event<Resource<Nothing>>> = _reAuthenticateUserLiveData

    private val _changeUserEmailLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val changeUserEmailLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserEmailLiveData

    private val _changeUserPasswordLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val changeUserPasswordLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserPasswordLiveData

//    fun isUserLoggedIn() = preferencesRepository.isUserLoggedInSynchronously()

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
                    _currentLanguageLiveData.postValue(
                        Event(UiText.StringResource(R.string.settings_text_settings_language_english_us))
                    )
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_ENGLISH_UNITED_STATES))
                    Timber.i("Current locale index is 0 (en_US).")
                }
                "en_GB" -> {
                    _currentLanguageLiveData.postValue(
                        Event(UiText.StringResource(R.string.settings_text_settings_language_english_gb))
                    )
                    _currentLocaleIndexLiveData.postValue(Event(LOCALE_INDEX_ENGLISH_GREAT_BRITAIN))
                    Timber.i("Current locale index is 1 (en_GB).")
                }
                "fa_IR" -> {
                    _currentLanguageLiveData.postValue(
                        Event(UiText.StringResource(R.string.settings_text_settings_language_persian_ir))
                    )
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

    fun getCurrentAppTheme() = viewModelScope.launch {
        safeGetCurrentAppTheme()
    }

    fun getRateAppDialogChoice() = viewModelScope.launch {
        safeGetRateAppDialogChoice()
    }

    fun getPreviousRequestTimeInMillis() = viewModelScope.launch {
        safeGetPreviousRequestTimeInMillis()
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

    fun changeCurrentTheme(index: Int) = viewModelScope.launch {
        preferencesRepository.setCurrentAppTheme(index)
        _currentAppThemeLiveData.postValue(Event(index))
        SettingsHelper.setAppTheme(index)
    }

    fun setRateAppDialogChoice(value: Int) = viewModelScope.launch {
        preferencesRepository.setRateAppDialogChoice(value)
        _rateAppDialogChoiceLiveData.postValue(Event(value))
    }

    fun setPreviousRequestTimeInMillis() = viewModelScope.launch {
        preferencesRepository.setPreviousRequestTimeInMillis(Calendar.getInstance().timeInMillis)
    }

    fun getAccountDetails() = viewModelScope.launch {
        safeGetAccountDetails()
    }

    fun sendVerificationEmail() = viewModelScope.launch {
        safeSendVerificationEmail()
    }

    fun logoutUser() = viewModelScope.launch {
        safeLogoutUser()
    }

    fun checkChangeEmailInputs(
        password: String,
        newEmail: String,
        currentUserEmail: String = userRepository.getCurrentUserEmail()
    ) {
        if (password.isBlank()) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_PASSWORD)))
            return
        }

        if (newEmail.isBlank()) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_EMAIL)))
            return
        }

        if (!isEmailValid(newEmail)) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_EMAIL_INVALID)))
            return
        }

        if (newEmail == currentUserEmail) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_EMAIL_SAME)))
            return
        }

        _checkInputsLiveData.postValue(Event(Resource.success(password)))
    }

    fun checkChangePasswordInputs(
        currentPassword: String, newPassword: String, retypeNewPassword: String
    ) {
        if (currentPassword.isBlank()) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_PASSWORD)))
            return
        }

        if (newPassword.isBlank()) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_NEW_PASSWORD)))
            return
        }

        if (retypeNewPassword.isBlank()) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_RETYPE_PASSWORD)))
            return
        }

        if (passwordStrength(newPassword) == (-1).toByte()) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_PASSWORD_SHORT)))
            return
        }

        if (!isRetypePasswordValid(newPassword, retypeNewPassword)) {
            _checkInputsLiveData.postValue(Event(Resource.error(ERROR_INPUT_PASSWORD_NOT_MATCH)))
            return
        }

        _checkInputsLiveData.postValue(Event(Resource.success(currentPassword)))
    }

    fun reAuthenticateUser(password: String) = viewModelScope.launch {
        safeReAuthenticateUser(password)
    }

    fun changeUserEmail(newEmail: String) = viewModelScope.launch {
        safeChangeUserEmail(newEmail)
    }

    fun changeUserPassword(newPassword: String) = viewModelScope.launch {
        safeChangeUserPassword(newPassword)
    }

    private suspend fun safeGetCurrentAppTheme() {
        _currentAppThemeLiveData.postValue(Event(preferencesRepository.getCurrentAppTheme()))
    }

    private suspend fun safeGetRateAppDialogChoice() {
        _rateAppDialogChoiceLiveData.postValue(Event(preferencesRepository.getRateAppDialogChoice()))
    }

    private suspend fun safeGetPreviousRequestTimeInMillis() {
        _previousRequestTimeInMillisLiveData.postValue(Event(preferencesRepository.getPreviousRequestTimeInMillis()))
    }

    private suspend fun safeGetAccountDetails() {
        _accountDetailsLiveData.postValue(Event(Resource.loading()))
        try {
            val currentUser = userRepository.getCurrentUser() as FirebaseUser
            var email = currentUser.email
            var isVerified = currentUser.isEmailVerified
            _accountDetailsLiveData.postValue(Event(Resource.success(currentUser)))
            Timber.i("Current user is $email and isVerified: $isVerified")

            userRepository.reloadCurrentUser()
            if (email != currentUser.email || isVerified != currentUser.isEmailVerified) {
                email = currentUser.email
                isVerified = currentUser.isEmailVerified
                _accountDetailsLiveData.postValue(Event(Resource.success(currentUser)))
                Timber.i("Updated user is $email and isVerified: $isVerified")
            }
        } catch (e: Exception) {
            Timber.e("safeGetAccountDetails Exception: ${e.message}")
            _accountDetailsLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeSendVerificationEmail() {
        _sendVerificationEmailLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.sendVerificationEmail()
            _sendVerificationEmailLiveData.postValue(Event(Resource.success(null)))
            Timber.i("Verification email sent.")
        } catch (e: Exception) {
            Timber.e("safeSendVerificationEmail Exception: ${e.message}")
            _sendVerificationEmailLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeLogoutUser() {
        _logoutLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.logoutUser()
            preferencesRepository.isUserLoggedIn(false)
            _logoutLiveData.postValue(Event(Resource.success(null)))
            Timber.i("User successfully logged out.")
        } catch (e: Exception) {
            Timber.e("safeLogoutUser Exception: ${e.message}")
            _logoutLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeReAuthenticateUser(password: String) {
        _reAuthenticateUserLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.reAuthenticateUser(password)
            _reAuthenticateUserLiveData.postValue(Event(Resource.success(null)))
            Timber.i("User re-authenticated.")
        } catch (e: Exception) {
            Timber.e("safeReAuthenticateUser Exception: ${e.message}")
            _reAuthenticateUserLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeChangeUserEmail(newEmail: String) {
        _changeUserEmailLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.updateUserEmail(newEmail)
            _changeUserEmailLiveData.postValue(Event(Resource.success(null)))
            Timber.i("User email updated to ${newEmail}.")
        } catch (e: Exception) {
            Timber.e("safeChangeUserEmail Exception: ${e.message}")
            _changeUserEmailLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeChangeUserPassword(newPassword: String) {
        _changeUserPasswordLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.updateUserPassword(newPassword)
            _changeUserPasswordLiveData.postValue(Event(Resource.success(null)))
            Timber.i("User password updated.")
        } catch (e: Exception) {
            Timber.e("safeChangeUserPassword Exception: ${e.message}")
            _changeUserPasswordLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private fun isActivityRestartNeeded(newLayoutDirection: Int): Boolean {
        val currentLocale = AppCompatDelegate.getApplicationLocales()[0]
        val currentLayoutDirection = currentLocale?.layoutDirection

        return if (Build.VERSION.SDK_INT >= 33) {
            false
        } else currentLayoutDirection != newLayoutDirection
    }
}