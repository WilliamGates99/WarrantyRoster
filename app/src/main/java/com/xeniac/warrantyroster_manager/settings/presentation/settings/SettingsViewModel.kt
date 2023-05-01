package com.xeniac.warrantyroster_manager.settings.presentation.settings

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

    private val _currentAppThemeIndexLiveData: MutableLiveData<Event<Resource<Int>>> =
        MutableLiveData()
    val currentAppThemeIndexLiveData: LiveData<Event<Resource<Int>>> = _currentAppThemeIndexLiveData

    private val _currentAppThemeUiTextLiveData: MutableLiveData<Event<Resource<UiText>>> =
        MutableLiveData()
    val currentAppThemeUiTextLiveData: LiveData<Event<Resource<UiText>>> =
        _currentAppThemeUiTextLiveData

    private val _changeCurrentAppThemeLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val changeCurrentAppThemeLiveData: LiveData<Event<Resource<Nothing>>> =
        _changeCurrentAppThemeLiveData

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

    fun getCurrentAppThemeIndex() = viewModelScope.launch {
        safeGetCurrentAppThemeIndex()
    }

    private suspend fun safeGetCurrentAppThemeIndex() {
        _currentAppThemeIndexLiveData.postValue(Event(Resource.Loading()))
        try {
            val themeIndex = preferencesRepository.getCurrentAppThemeIndex()
            _currentAppThemeIndexLiveData.postValue(Event(Resource.Success(themeIndex)))
            Timber.i("Current theme index is $themeIndex")
        } catch (e: Exception) {
            Timber.e("safeGetCurrentAppThemeIndex Exception: ${e.message}")
            _currentAppThemeIndexLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getCurrentAppThemeUiText() = viewModelScope.launch {
        safeGetCurrentAppThemeUiText()
    }

    private suspend fun safeGetCurrentAppThemeUiText() {
        _currentAppThemeUiTextLiveData.postValue(Event(Resource.Loading()))
        try {
            val themeUiText = preferencesRepository.getCurrentAppThemeUiText()
            _currentAppThemeUiTextLiveData.postValue(Event(Resource.Success(themeUiText)))
        } catch (e: Exception) {
            Timber.e("safeGetCurrentAppThemeUiText Exception: ${e.message}")
            _currentAppThemeUiTextLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
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

    fun changeCurrentAppTheme(index: Int) = viewModelScope.launch {
        safeChangeCurrentAppTheme(index)
    }

    private suspend fun safeChangeCurrentAppTheme(index: Int) {
        _changeCurrentAppThemeLiveData.postValue(Event(Resource.Loading()))
        try {
            preferencesRepository.setCurrentAppTheme(index)
            SettingsHelper.setAppTheme(index)
            _changeCurrentAppThemeLiveData.postValue(Event(Resource.Success()))
            Timber.i("App theme index changed to $index")
        } catch (e: Exception) {
            Timber.e("safeChangeCurrentAppTheme Exception: ${e.message}")
            _changeCurrentAppThemeLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
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