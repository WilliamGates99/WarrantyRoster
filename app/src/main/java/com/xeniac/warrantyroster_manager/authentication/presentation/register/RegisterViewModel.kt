package com.xeniac.warrantyroster_manager.authentication.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.util.Constants.PASSWORD_STRENGTH_WEAK
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.util.UserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentAppLanguageLiveData: MutableLiveData<Event<Resource<String>>> =
        MutableLiveData()
    val currentAppLanguageLiveData: LiveData<Event<Resource<String>>> = _currentAppLanguageLiveData

    private val _registerWithEmailLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val registerWithEmailLiveData: LiveData<Event<Resource<Nothing>>> = _registerWithEmailLiveData

    private val _registerWithGoogleAccountLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val registerWithGoogleAccountLiveData: LiveData<Event<Resource<Nothing>>> =
        _registerWithGoogleAccountLiveData

    private val _registerWithTwitterAccountLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val registerWithTwitterAccountLiveData: LiveData<Event<Resource<Nothing>>> =
        _registerWithTwitterAccountLiveData

    private val _registerWithFacebookAccountLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val registerWithFacebookAccountLiveData: LiveData<Event<Resource<Nothing>>> =
        _registerWithFacebookAccountLiveData

    fun getCurrentAppLanguage() = viewModelScope.launch {
        safeGetCurrentAppLanguage()
    }

    private suspend fun safeGetCurrentAppLanguage() {
        _currentAppLanguageLiveData.postValue(Event(Resource.Loading()))
        try {
            val language = preferencesRepository.getCurrentAppLanguage()
            _currentAppLanguageLiveData.postValue(Event(Resource.Success(language)))
            Timber.i("Current app language is $language")
        } catch (e: Exception) {
            Timber.e("safeGetCurrentAppLanguage Exception: ${e.message}")
            _currentAppLanguageLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun validateRegisterWithEmailInputs(
        email: String, password: String, retypePassword: String
    ) = viewModelScope.launch {
        safeValidateRegisterWithEmailInputs(email, password, retypePassword)
    }

    private fun safeValidateRegisterWithEmailInputs(
        email: String, password: String, retypePassword: String
    ) {
        if (email.isBlank()) {
            _registerWithEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EMAIL)))
            )
            return
        }

        if (password.isBlank()) {
            _registerWithEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_PASSWORD)))
            )
            return
        }

        if (retypePassword.isBlank()) {
            _registerWithEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_RETYPE_PASSWORD)))
            )
            return
        }

        if (!UserHelper.isEmailValid(email)) {
            _registerWithEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_EMAIL_INVALID)))
            )
            return
        }

        if (UserHelper.passwordStrength(password) == PASSWORD_STRENGTH_WEAK) {
            _registerWithEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_PASSWORD_SHORT)))
            )
            return
        }

        if (!UserHelper.isRetypePasswordValid(password, retypePassword)) {
            _registerWithEmailLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_PASSWORD_NOT_MATCH)))
            )
            return
        }

        registerWithEmail(email, password)
    }

    fun registerWithEmail(email: String, password: String) = viewModelScope.launch {
        safeRegisterWithEmail(email, password)
    }

    private suspend fun safeRegisterWithEmail(email: String, password: String) {
        _registerWithEmailLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.registerWithEmail(email, password)
            userRepository.sendVerificationEmail()
            preferencesRepository.isUserLoggedIn(true)
            _registerWithEmailLiveData.postValue(Event(Resource.Success()))
            Timber.i("$email registered successfully.")
        } catch (e: Exception) {
            Timber.e("safeRegisterWithEmail Exception: ${e.message}")
            _registerWithEmailLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun registerWithGoogleAccount(account: GoogleSignInAccount) = viewModelScope.launch {
        safeRegisterWithGoogleAccount(account)
    }

    private suspend fun safeRegisterWithGoogleAccount(account: GoogleSignInAccount) {
        _registerWithGoogleAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginWithGoogleAccount(account)
            preferencesRepository.isUserLoggedIn(true)
            _registerWithGoogleAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Successfully registered with Google account.")
        } catch (e: Exception) {
            Timber.e("safeRegisterWithGoogleAccount Exception: ${e.message}")
            _registerWithGoogleAccountLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun registerWithTwitterAccount(credential: AuthCredential) = viewModelScope.launch {
        safeRegisterWithTwitterAccount(credential)
    }

    private suspend fun safeRegisterWithTwitterAccount(credential: AuthCredential) {
        _registerWithTwitterAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginWithTwitterAccount(credential)
            preferencesRepository.isUserLoggedIn(true)
            _registerWithTwitterAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Successfully registered with Twitter account.")
        } catch (e: Exception) {
            Timber.e("safeRegisterWithTwitterAccount Exception: ${e.message}")
            _registerWithTwitterAccountLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(e.message.toString())))
            )
        }
    }

    fun registerWithFacebookAccount(accessToken: AccessToken) = viewModelScope.launch {
        safeRegisterWithFacebookAccount(accessToken)
    }

    private suspend fun safeRegisterWithFacebookAccount(accessToken: AccessToken) {
        _registerWithFacebookAccountLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.loginWithFacebookAccount(accessToken)
            preferencesRepository.isUserLoggedIn(true)
            _registerWithFacebookAccountLiveData.postValue(Event(Resource.Success()))
            Timber.i("Successfully registered with Facebook account.")
        } catch (e: Exception) {
            Timber.e("safeRegisterWithFacebookAccount Exception: ${e.message}")
            _registerWithFacebookAccountLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(e.message.toString())))
            )
        }
    }
}