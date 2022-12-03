package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
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

    private val _loginLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginLiveData: LiveData<Event<Resource<Nothing>>> = _loginLiveData

    private val _loginWithGoogleLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val loginWithGoogleLiveData: LiveData<Event<Resource<Nothing>>> = _loginWithGoogleLiveData

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

    fun authenticateGoogleAccountWithFirebase(account: GoogleSignInAccount) =
        viewModelScope.launch {
            safeAuthenticateGoogleAccountWithFirebase(account)
        }

    private suspend fun safeAuthenticateGoogleAccountWithFirebase(account: GoogleSignInAccount) {
        _loginWithGoogleLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.authenticateGoogleAccountWithFirebase(account)
            preferencesRepository.isUserLoggedIn(true)
            _loginWithGoogleLiveData.postValue(Event(Resource.Success()))
            Timber.i("${account.email} logged in successfully with Google account.")
        } catch (e: Exception) {
            Timber.e("safeAuthenticateGoogleAccountWithFirebase Exception: ${e.message}")
            _loginWithGoogleLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}