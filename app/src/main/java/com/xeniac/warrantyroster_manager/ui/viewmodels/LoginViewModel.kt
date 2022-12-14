package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.UiText
import com.xeniac.warrantyroster_manager.utils.UserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentLanguageLiveData: MutableLiveData<Event<String>> = MutableLiveData()
    val currentLanguageLiveData: LiveData<Event<String>> = _currentLanguageLiveData

    private val _loginLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginLiveData: LiveData<Event<Resource<Nothing>>> = _loginLiveData

    private val _loginWithGoogleAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginWithGoogleAccountLiveData:
            LiveData<Event<Resource<Nothing>>> = _loginWithGoogleAccountLiveData

    private val _loginWithTwitterAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginWithTwitterAccountLiveData:
            LiveData<Event<Resource<Nothing>>> = _loginWithTwitterAccountLiveData

    private val _loginWithFacebookAccountLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginWithFacebookAccountLiveData:
            LiveData<Event<Resource<Nothing>>> = _loginWithFacebookAccountLiveData

    fun getCurrentAppLanguage() = viewModelScope.launch {
        safeGetCurrentAppLanguage()
    }

    private fun safeGetCurrentAppLanguage() {
        val localeList = AppCompatDelegate.getApplicationLocales()

        if (localeList.isEmpty) {
            _currentLanguageLiveData.postValue(Event(LOCALE_ENGLISH))
            Timber.i("Locale list is Empty. -> Current app language is $LOCALE_ENGLISH")
        } else {
            val currentLanguage = localeList[0]!!.language
            _currentLanguageLiveData.postValue(Event(currentLanguage))
            Timber.i("Current app language is $currentLanguage")
        }
    }

    fun validateLoginInputs(email: String, password: String) = viewModelScope.launch {
        safeValidateLoginInputs(email, password)
    }

    private fun safeValidateLoginInputs(email: String, password: String) {
        if (email.isBlank()) {
            _loginLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EMAIL)))
            )
            return
        }

        if (password.isBlank()) {
            _loginLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_PASSWORD)))
            )
            return
        }

        if (!UserHelper.isEmailValid(email)) {
            _loginLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_EMAIL_INVALID)))
            )
            return
        }

        loginViaEmail(email, password)
    }

    fun loginViaEmail(email: String, password: String) = viewModelScope.launch {
        safeLoginViaEmail(email, password)
    }

    private suspend fun safeLoginViaEmail(email: String, password: String) {
        _loginLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginViaEmail(email, password)
            preferencesRepository.isUserLoggedIn(true)
            _loginLiveData.postValue(Event(Resource.Success()))
            Timber.i("$email logged in successfully.")
        } catch (e: Exception) {
            Timber.e("safeLoginViaEmail Exception: ${e.message}")
            _loginLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun loginWithGoogleAccount(account: GoogleSignInAccount) =
        viewModelScope.launch {
            safeLoginWithGoogleAccount(account)
        }

    private suspend fun safeLoginWithGoogleAccount(account: GoogleSignInAccount) {
        _loginWithGoogleAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginWithGoogleAccount(account)
            preferencesRepository.isUserLoggedIn(true)
            _loginWithGoogleAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Successfully logged in with Google account.")
        } catch (e: Exception) {
            Timber.e("safeAuthenticateGoogleAccountWithFirebase Exception: ${e.message}")
            _loginWithGoogleAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun loginWithTwitterAccount(credential: AuthCredential) = viewModelScope.launch {
        safeLoginWithTwitterAccount(credential)
    }

    private suspend fun safeLoginWithTwitterAccount(credential: AuthCredential) {
        _loginWithTwitterAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginWithTwitterAccount(credential)
            preferencesRepository.isUserLoggedIn(true)
            _loginWithTwitterAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Successfully logged in with Twitter account.")
        } catch (e: Exception) {
            Timber.e("safeLoginWithTwitterAccount Exception: ${e.message}")
            _loginWithTwitterAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun loginWithFacebookAccount(accessToken: AccessToken) = viewModelScope.launch {
        safeLoginWithFacebookAccount(accessToken)
    }

    private suspend fun safeLoginWithFacebookAccount(accessToken: AccessToken) {
        _loginWithFacebookAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginWithFacebookAccount(accessToken)
            preferencesRepository.isUserLoggedIn(true)
            _loginWithFacebookAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Successfully logged in with Facebook account.")
        } catch (e: Exception) {
            Timber.e("safeLoginWithFacebookAccount Exception: ${e.message}")
            _loginWithFacebookAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}