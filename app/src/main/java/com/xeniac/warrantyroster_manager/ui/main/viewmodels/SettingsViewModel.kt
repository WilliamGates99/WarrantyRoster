package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.repositories.PreferencesRepository
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.ui.landing.LandingActivity
import com.xeniac.warrantyroster_manager.utils.*
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_SAME
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_COUNTRY_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_PERSIAN
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.UserHelper.isEmailValid
import com.xeniac.warrantyroster_manager.utils.UserHelper.isRetypePasswordValid
import com.xeniac.warrantyroster_manager.utils.UserHelper.passwordStrength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {

    private val _currentAppTheme: MutableLiveData<Event<Int>> = MutableLiveData()
    val currentAppTheme: LiveData<Event<Int>> = _currentAppTheme

    private val _currentAppLocale: MutableLiveData<Event<Array<String>>> = MutableLiveData()
    val currentAppLocale: LiveData<Event<Array<String>>> = _currentAppLocale

    private val _accountDetailsLiveData: MutableLiveData<Event<Resource<FirebaseUser>>> =
        MutableLiveData()
    val accountDetailsLiveData: LiveData<Event<Resource<FirebaseUser>>> = _accountDetailsLiveData

    private val _sendVerificationEmailLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val sendVerificationEmailLiveData: LiveData<Event<Resource<Nothing>>> =
        _sendVerificationEmailLiveData

    private val _logoutLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val logoutLiveData: LiveData<Event<Resource<Nothing>>> = _logoutLiveData

    private val _reAuthenticateUserLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val reAuthenticateUserLiveData: LiveData<Event<Resource<Nothing>>> = _reAuthenticateUserLiveData

    private val _changeUserEmailLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val changeUserEmailLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserEmailLiveData

    private val _changeUserPasswordLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val changeUserPasswordLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserPasswordLiveData

    fun isUserLoggedIn() = preferencesRepository.getIsUserLoggedInSynchronously()

    fun getCurrentAppLocale() = viewModelScope.launch {
        safeGetCurrentAppLocale()
    }

    fun getCurrentAppTheme() = viewModelScope.launch {
        safeGetCurrentAppTheme()
    }

    fun setAppLocale(index: Int, activity: Activity) = viewModelScope.launch {
        var newLanguage = LOCALE_LANGUAGE_ENGLISH
        var newCountry = LOCALE_COUNTRY_UNITED_STATES

        //TODO CHANGE AFTER ADDING BRITISH ENGLISH
        when (index) {
            0 -> {
                newLanguage = LOCALE_LANGUAGE_ENGLISH
                newCountry = LOCALE_COUNTRY_UNITED_STATES
            }
            1 -> {
                newLanguage = LOCALE_LANGUAGE_PERSIAN
                newCountry = LOCALE_COUNTRY_IRAN
            }
        }

        preferencesRepository.setCurrentAppLanguage(newLanguage)
        preferencesRepository.setCurrentAppCountry(newCountry)
        SettingsHelper.setAppLocale(getApplication<BaseApplication>(), newLanguage, newCountry)

        activity.apply {
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }
    }

    fun setAppTheme(index: Int) = viewModelScope.launch {
        preferencesRepository.setAppTheme(index)
        _currentAppTheme.postValue(Event(index))
        SettingsHelper.setAppTheme(index)
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
            _changeUserEmailLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_PASSWORD)))
            return
        }

        if (newEmail.isBlank()) {
            _changeUserEmailLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_EMAIL)))
            return
        }

        if (!isEmailValid(newEmail)) {
            _changeUserEmailLiveData.postValue(Event(Resource.error(ERROR_INPUT_EMAIL_INVALID)))
            return
        }

        if (newEmail == currentUserEmail) {
            _changeUserEmailLiveData.postValue(Event(Resource.error(ERROR_INPUT_EMAIL_SAME)))
            return
        }

        reAuthenticateUser(password)
    }

    fun checkChangePasswordInputs(
        currentPassword: String, newPassword: String, retypeNewPassword: String
    ) {
        if (currentPassword.isBlank()) {
            _changeUserPasswordLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_PASSWORD)))
            return
        }

        if (newPassword.isBlank()) {
            _changeUserPasswordLiveData.postValue(
                Event(Resource.error(ERROR_INPUT_BLANK_NEW_PASSWORD))
            )
            return
        }

        if (retypeNewPassword.isBlank()) {
            _changeUserPasswordLiveData.postValue(
                Event(Resource.error(ERROR_INPUT_BLANK_RETYPE_PASSWORD))
            )
            return
        }

        if (passwordStrength(newPassword) == (-1).toByte()) {
            _changeUserPasswordLiveData.postValue(Event(Resource.error(ERROR_INPUT_PASSWORD_SHORT)))
            return
        }

        if (!isRetypePasswordValid(newPassword, retypeNewPassword)) {
            _changeUserPasswordLiveData.postValue(
                Event(Resource.error(ERROR_INPUT_PASSWORD_NOT_MATCH))
            )
            return
        }

        reAuthenticateUser(currentPassword)
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
        _currentAppLocale.postValue(Event(currentLocale))
    }

    private suspend fun safeGetCurrentAppTheme() {
        _currentAppTheme.postValue(Event(preferencesRepository.getCurrentAppTheme()))
    }

    private suspend fun safeGetAccountDetails() {
        _accountDetailsLiveData.postValue(Event(Resource.loading()))
        try {
            val currentUser = userRepository.getCurrentUser() as FirebaseUser
            var email = currentUser.email
            var isVerified = currentUser.isEmailVerified
            _accountDetailsLiveData.postValue(Event(Resource.success(currentUser)))
            Timber.i("Current user is $email and isVerified: $isVerified")

            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.reloadCurrentUser()
                if (email != currentUser.email || isVerified != currentUser.isEmailVerified) {
                    email = currentUser.email
                    isVerified = currentUser.isEmailVerified
                    _accountDetailsLiveData.postValue(Event(Resource.success(currentUser)))
                    Timber.i("Updated user is $email and isVerified: $isVerified")
                }
            }
        } catch (e: Exception) {
            Timber.e("safeGetAccountDetails Exception: ${e.message}")
            _accountDetailsLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeSendVerificationEmail() {
        _sendVerificationEmailLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.sendVerificationEmail()
                _sendVerificationEmailLiveData.postValue(Event(Resource.success(null)))
                Timber.i("Verification email sent.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _sendVerificationEmailLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("safeSendVerificationEmail Exception: ${e.message}")
            _sendVerificationEmailLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeLogoutUser() {
        _logoutLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.logoutUser()
            preferencesRepository.setIsUserLoggedIn(false)
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
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.reAuthenticateUser(password)
                _reAuthenticateUserLiveData.postValue(Event(Resource.success(null)))
                Timber.i("User re-authenticated.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _reAuthenticateUserLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("safeReAuthenticateUser Exception: ${e.message}")
            _reAuthenticateUserLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeChangeUserEmail(newEmail: String) {
        _changeUserEmailLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.updateUserEmail(newEmail)
                _changeUserEmailLiveData.postValue(Event(Resource.success(null)))
                Timber.i("User email updated to ${newEmail}.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _changeUserEmailLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("safeChangeUserEmail Exception: ${e.message}")
            _changeUserEmailLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeChangeUserPassword(newPassword: String) {
        _changeUserPasswordLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                userRepository.updateUserPassword(newPassword)
                _changeUserPasswordLiveData.postValue(Event(Resource.success(null)))
                Timber.i("User password updated.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _changeUserPasswordLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Timber.e("safeChangeUserPassword Exception: ${e.message}")
            _changeUserPasswordLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }
}