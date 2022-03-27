package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.di.LoginPrefs
import com.xeniac.warrantyroster_manager.di.SettingsPrefs
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_THEME_KEY
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    @SettingsPrefs private val settingsPrefs: SharedPreferences,
    @LoginPrefs private val loginPrefs: SharedPreferences
) : AndroidViewModel(application) {

    private val _accountDetailsLiveData:
            MutableLiveData<Event<Resource<FirebaseUser>>> = MutableLiveData()
    val accountDetailsLiveData: LiveData<Event<Resource<FirebaseUser>>> = _accountDetailsLiveData

    private val _sendVerificationEmailLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val sendVerificationEmailLiveData:
            LiveData<Event<Resource<Nothing>>> = _sendVerificationEmailLiveData

    private val _logoutLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val logoutLiveData: LiveData<Event<Resource<Nothing>>> = _logoutLiveData

    private val _reAuthenticateUserLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val reAuthenticateUserLiveData: LiveData<Event<Resource<Nothing>>> = _reAuthenticateUserLiveData

    private val _changeUserEmailLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val changeUserEmailLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserEmailLiveData

    private val _changeUserPasswordLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val changeUserPasswordLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserPasswordLiveData

    fun setAppTheme(index: Int) = viewModelScope.launch {
        when (index) {
            0 -> {
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                    settingsPrefs.edit().putInt(PREFERENCE_THEME_KEY, index).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
            1 -> {
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                    settingsPrefs.edit().putInt(PREFERENCE_THEME_KEY, index).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            2 -> {
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                    settingsPrefs.edit().putInt(PREFERENCE_THEME_KEY, index).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
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

    fun reAuthenticateUser(password: String) = viewModelScope.launch {
        safeReAuthenticateUser(password)
    }

    fun changeUserEmail(newEmail: String) = viewModelScope.launch {
        safeChangeUserEmail(newEmail)
    }

    fun changeUserPassword(newPassword: String) = viewModelScope.launch {
        safeChangeUserPassword(newPassword)
    }

    private suspend fun safeGetAccountDetails() {
        _accountDetailsLiveData.postValue(Event(Resource.loading()))
        try {
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let { user ->
                var email = user.email
                var isVerified = user.isEmailVerified
                _accountDetailsLiveData.postValue(Event(Resource.success(user)))
                Timber.i("Current user is $email and isVerified: $isVerified")

                if (hasInternetConnection(getApplication<BaseApplication>())) {
                    userRepository.reloadCurrentUser(user).await()
                    if (email != user.email || isVerified != user.isEmailVerified) {
                        email = user.email
                        isVerified = user.isEmailVerified
                        _accountDetailsLiveData.postValue(Event(Resource.success(user)))
                        Timber.i("Updated user is $email and isVerified: $isVerified")
                    }
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
                userRepository.sendVerificationEmail().await()
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

    private fun safeLogoutUser() {
        _logoutLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.logoutUser()
            loginPrefs.edit().remove(PREFERENCE_IS_LOGGED_IN_KEY).apply()
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
                userRepository.reAuthenticateUser(password).await()
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
                userRepository.updateUserEmail(newEmail).await()
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
                userRepository.updateUserPassword(newPassword).await()
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