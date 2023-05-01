package com.xeniac.warrantyroster_manager.settings.presentation.settings

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.layoutDirection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.model.UserInfo
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_DEFAULT_OR_EMPTY
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SettingsHelper
import com.xeniac.warrantyroster_manager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentAppLocaleIndexLiveData: MutableLiveData<Event<Resource<Int>>> =
        MutableLiveData()
    val currentAppLocaleIndexLiveData: LiveData<Event<Resource<Int>>> =
        _currentAppLocaleIndexLiveData

    private val _currentAppLocaleUiTextLiveData: MutableLiveData<Event<Resource<UiText>>> =
        MutableLiveData()
    val currentAppLocaleUiTextLiveData: LiveData<Event<Resource<UiText>>> =
        _currentAppLocaleUiTextLiveData

    private val _changeCurrentAppLocaleLiveData: MutableLiveData<Event<Resource<Boolean>>> =
        MutableLiveData()
    val changeCurrentAppLocaleLiveData: LiveData<Event<Resource<Boolean>>> =
        _changeCurrentAppLocaleLiveData

    private val _currentAppThemeLiveData: MutableLiveData<Event<Int>> = MutableLiveData()
    val currentAppThemeLiveData: LiveData<Event<Int>> = _currentAppThemeLiveData

    private val _userInfoLiveData: MutableLiveData<Event<Resource<UserInfo>>> = MutableLiveData()
    val userInfoLiveData: LiveData<Event<Resource<UserInfo>>> = _userInfoLiveData

    private val _sendVerificationEmailLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val sendVerificationEmailLiveData: LiveData<Event<Resource<Nothing>>> =
        _sendVerificationEmailLiveData

    private val _logoutLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val logoutLiveData: LiveData<Event<Resource<Nothing>>> = _logoutLiveData

    fun getCurrentAppLocaleIndex() = viewModelScope.launch {
        safeGetCurrentAppLocaleIndex()
    }

    private suspend fun safeGetCurrentAppLocaleIndex() {
        _currentAppLocaleIndexLiveData.postValue(Event(Resource.Loading()))
        try {
            val localeIndex = preferencesRepository.getCurrentAppLocaleIndex()
            Timber.i("Current locale index is $localeIndex")

            if (localeIndex == LOCALE_INDEX_DEFAULT_OR_EMPTY) {
                changeCurrentAppLocale(LOCALE_INDEX_ENGLISH_UNITED_STATES)
            } else {
                _currentAppLocaleIndexLiveData.postValue(Event(Resource.Success(localeIndex)))
            }
        } catch (e: Exception) {
            Timber.e("safeGetCurrentAppLocaleIndex Exception: ${e.message}")
            _currentAppLocaleIndexLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getCurrentAppLocaleUiText() = viewModelScope.launch {
        safeGetCurrentAppLocaleUiText()
    }

    private suspend fun safeGetCurrentAppLocaleUiText() {
        _currentAppLocaleUiTextLiveData.postValue(Event(Resource.Loading()))
        try {
            val localeUiText = preferencesRepository.getCurrentAppLocaleUiText()
            _currentAppLocaleUiTextLiveData.postValue(Event(Resource.Success(localeUiText)))
        } catch (e: Exception) {
            Timber.e("safeGetCurrentAppLocaleUiText Exception: ${e.message}")
            _currentAppLocaleUiTextLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getCurrentAppTheme() = viewModelScope.launch {
        safeGetCurrentAppTheme()
    }

    private suspend fun safeGetCurrentAppTheme() {
        _currentAppThemeLiveData.postValue(Event(preferencesRepository.getCurrentAppTheme()))
    }

    fun changeCurrentAppLocale(index: Int) = viewModelScope.launch {
        safeChangeCurrentAppLocale(index)
    }

    private suspend fun safeChangeCurrentAppLocale(index: Int) {
        _currentAppLocaleIndexLiveData.postValue(Event(Resource.Loading()))
        try {
            val isActivityRestartNeeded = preferencesRepository.setCurrentAppLocale(index)
            _changeCurrentAppLocaleLiveData.postValue(Event(Resource.Success(isActivityRestartNeeded)))
            Timber.i("App locale index changed to $index and isActivityRestartNeeded = $isActivityRestartNeeded}")
        } catch (e: Exception) {
            Timber.e("safeChangeCurrentAppLocale Exception: ${e.message}")
            _changeCurrentAppLocaleLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun changeCurrentTheme(index: Int) = viewModelScope.launch {
        safeChangeCurrentTheme(index)
    }

    private suspend fun safeChangeCurrentTheme(index: Int) {
        preferencesRepository.setCurrentAppTheme(index)
        _currentAppThemeLiveData.postValue(Event(index))
        SettingsHelper.setAppTheme(index)
    }

    fun getCachedUserInfo() = viewModelScope.launch {
        safeGetCachedUserInfo()
    }

    private fun safeGetCachedUserInfo() {
        _userInfoLiveData.postValue(Event(Resource.Loading()))
        try {
            val userInfo = userRepository.getCachedUserInfo()
            _userInfoLiveData.postValue(Event(Resource.Success(userInfo)))
            Timber.i("Cached user info is $userInfo")
        } catch (e: Exception) {
            Timber.e("safeGetCachedUserInfo Exception: ${e.message}")
            _userInfoLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getReloadedUserInfo() = viewModelScope.launch {
        safeGetReloadedUserInfo()
    }

    private suspend fun safeGetReloadedUserInfo() {
        _userInfoLiveData.postValue(Event(Resource.Loading()))
        try {
            val userInfo = userRepository.getReloadedUserInfo()
            _userInfoLiveData.postValue(Event(Resource.Success(userInfo)))
            Timber.i("Reloaded user info is $userInfo")
        } catch (e: Exception) {
            Timber.e("safeGetReloadedUserInfo Exception: ${e.message}")
            _userInfoLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun sendVerificationEmail() = viewModelScope.launch {
        safeSendVerificationEmail()
    }

    private suspend fun safeSendVerificationEmail() {
        _sendVerificationEmailLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.sendVerificationEmail()
            _sendVerificationEmailLiveData.postValue(Event(Resource.Success()))
            Timber.i("Verification email sent.")
        } catch (e: Exception) {
            Timber.e("safeSendVerificationEmail Exception: ${e.message}")
            _sendVerificationEmailLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun logoutUser() = viewModelScope.launch {
        safeLogoutUser()
    }

    private suspend fun safeLogoutUser() {
        _logoutLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.logoutUser()
            preferencesRepository.isUserLoggedIn(false)
            _logoutLiveData.postValue(Event(Resource.Success()))
            Timber.i("User successfully logged out.")
        } catch (e: Exception) {
            Timber.e("safeLogoutUser Exception: ${e.message}")
            _logoutLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}