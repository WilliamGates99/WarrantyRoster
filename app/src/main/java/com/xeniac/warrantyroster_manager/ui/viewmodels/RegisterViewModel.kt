package com.xeniac.warrantyroster_manager.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.UiText
import com.xeniac.warrantyroster_manager.utils.UserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _registerLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val registerLiveData: LiveData<Event<Resource<Nothing>>> = _registerLiveData

    fun validateRegisterInputs(email: String, password: String, retypePassword: String) {
        if (email.isBlank()) {
            _registerLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EMAIL)))
            )
            return
        }

        if (password.isBlank()) {
            _registerLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_PASSWORD)))
            )
            return
        }

        if (retypePassword.isBlank()) {
            _registerLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_RETYPE_PASSWORD)))
            )
            return
        }

        if (!UserHelper.isEmailValid(email)) {
            _registerLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_EMAIL_INVALID)))
            )
            return
        }

        if (UserHelper.passwordStrength(password) == (-1).toByte()) {
            _registerLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_PASSWORD_SHORT)))
            )
            return
        }

        if (!UserHelper.isRetypePasswordValid(password, retypePassword)) {
            _registerLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_PASSWORD_NOT_MATCH)))
            )
            return
        }

        registerViaEmail(email, password)
    }

    fun registerViaEmail(email: String, password: String) = viewModelScope.launch {
        safeRegisterViaEmail(email, password)
    }

    private suspend fun safeRegisterViaEmail(email: String, password: String) {
        _registerLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.registerViaEmail(email, password)
            userRepository.sendVerificationEmail()
            preferencesRepository.isUserLoggedIn(true)
            _registerLiveData.postValue(Event(Resource.Success()))
            Timber.i("$email registered successfully.")
        } catch (e: Exception) {
            Timber.e("safeRegisterViaEmail Exception: ${e.message}")
            _registerLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}