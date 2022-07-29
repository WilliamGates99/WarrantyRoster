package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
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
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_PERSIAN
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SettingsHelper
import com.xeniac.warrantyroster_manager.utils.Status
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

    private val _currentAppLocaleLiveData: MutableLiveData<Event<Array<String>>> = MutableLiveData()
    val currentAppLocaleLiveData: LiveData<Event<Array<String>>> = _currentAppLocaleLiveData

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

    fun isUserLoggedIn() = preferencesRepository.isUserLoggedInSynchronously()

    fun getCurrentAppLocale() = viewModelScope.launch {
        safeGetCurrentAppLocale()
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

    fun setAppLocale(index: Int) = viewModelScope.launch {
        safeSetAppLocale(index)
    }

    fun setAppTheme(index: Int) = viewModelScope.launch {
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

    private suspend fun safeGetCurrentAppLocale() {
        val currentLanguage = preferencesRepository.getCurrentAppLanguage()
        val currentCountry = preferencesRepository.getCurrentAppCountry()
        val currentLocale = arrayOf(currentLanguage, currentCountry)
        _currentAppLocaleLiveData.postValue(Event(currentLocale))
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

    private suspend fun safeSetAppLocale(index: Int) {
        _setAppLocaleLiveData.postValue(Event(Resource(Status.LOADING)))
        try {
            var newLanguage = LOCALE_LANGUAGE_ENGLISH
            var newCountry = LOCALE_COUNTRY_UNITED_STATES

            when (index) {
                0 -> {
                    newLanguage = LOCALE_LANGUAGE_ENGLISH
                    newCountry = LOCALE_COUNTRY_UNITED_STATES
                }
                1 -> {
                    newLanguage = LOCALE_LANGUAGE_ENGLISH
                    newCountry = LOCALE_COUNTRY_GREAT_BRITAIN
                }
                2 -> {
                    newLanguage = LOCALE_LANGUAGE_PERSIAN
                    newCountry = LOCALE_COUNTRY_IRAN
                }
            }

            preferencesRepository.setCurrentAppLanguage(newLanguage)
            preferencesRepository.setCurrentAppCountry(newCountry)

            val newLocale = arrayOf(newLanguage, newCountry)
            _setAppLocaleLiveData.postValue(Event(Resource(Status.SUCCESS, newLocale)))
            Timber.i("New app locale is: $newLanguage-$newCountry")
        } catch (e: Exception) {
            Timber.e("safeSetAppLocale Exception: ${e.message}")
            _setAppLocaleLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
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
}