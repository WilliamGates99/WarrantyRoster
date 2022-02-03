package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.WarrantyRosterApplication
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_COUNTRY_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_SETTINGS
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_THEME_KEY
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val settingsPrefs = getApplication<WarrantyRosterApplication>()
        .getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE)

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
            MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val reAuthenticateUserLiveData: LiveData<Event<Resource<String>>> = _reAuthenticateUserLiveData

    private val _changeUserEmailLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val changeUserEmailLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserEmailLiveData

    private val TAG = "SettingsViewModel"

    fun getCurrentLanguage(): String =
        settingsPrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en").toString()

    fun getCurrentCountry(): String =
        settingsPrefs.getString(PREFERENCE_COUNTRY_KEY, "US").toString()

    fun getCurrentTheme(): Int = settingsPrefs.getInt(PREFERENCE_THEME_KEY, 0)

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

    fun reAuthenticateUser(password: String, newEmail: String) = viewModelScope.launch {
        safeReAuthenticateUser(password, newEmail)
    }

    fun changeUserEmail(newEmail: String) = viewModelScope.launch {
        safeChangeUserEmail(newEmail)
    }

    private suspend fun safeGetAccountDetails() {
        _accountDetailsLiveData.postValue(Event(Resource.loading()))
        try {
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let { user ->
                var email = user.email
                var isVerified = user.isEmailVerified
                _accountDetailsLiveData.postValue(Event(Resource.success(user)))
                Log.i(TAG, "Current user is $email and isVerified: $isVerified")

                if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                    userRepository.reloadCurrentUser(user).await()
                    if (email != user.email || isVerified != user.isEmailVerified) {
                        email = user.email
                        isVerified = user.isEmailVerified
                        _accountDetailsLiveData.postValue(Event(Resource.success(user)))
                        Log.i(TAG, "Updated user is $email and isVerified: $isVerified")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            _accountDetailsLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeSendVerificationEmail() {
        _sendVerificationEmailLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                userRepository.sendVerificationEmail().await()
                _sendVerificationEmailLiveData.postValue(Event(Resource.success(null)))
                Log.i(TAG, "Verification email sent.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _sendVerificationEmailLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            _sendVerificationEmailLiveData.postValue(Event(Resource.error(t.message.toString())))
        }
    }

    private fun safeLogoutUser() {
        _logoutLiveData.postValue(Event(Resource.loading()))
        try {
            userRepository.logoutUser()

            getApplication<WarrantyRosterApplication>().getSharedPreferences(
                PREFERENCE_LOGIN, Context.MODE_PRIVATE
            ).edit().apply {
                remove(PREFERENCE_IS_LOGGED_IN_KEY)
                apply()
            }

            _logoutLiveData.postValue(Event(Resource.success(null)))
            Log.i(TAG, "User successfully logged out.")
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            _logoutLiveData.postValue(Event(Resource.error(t.message.toString())))
        }
    }

    private suspend fun safeReAuthenticateUser(password: String, newEmail: String) {
        _reAuthenticateUserLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                userRepository.reAuthenticateUser(password).await()
                _reAuthenticateUserLiveData.postValue(Event(Resource.success(newEmail)))
                Log.i(TAG, "User re-authenticated.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _reAuthenticateUserLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            _reAuthenticateUserLiveData.postValue(Event(Resource.error(t.message.toString())))
        }
    }

    private suspend fun safeChangeUserEmail(newEmail: String) {
        _changeUserEmailLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                userRepository.updateEmail(newEmail).await()
                _changeUserEmailLiveData.postValue(Event(Resource.success(null)))
                Log.i(TAG, "User email updated to ${newEmail}.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _changeUserEmailLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            _changeUserEmailLiveData.postValue(Event(Resource.error(t.message.toString())))
        }
    }
}