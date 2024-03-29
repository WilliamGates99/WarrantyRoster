package com.xeniac.warrantyroster_manager.settings.presentation.change_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_NEW_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
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
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _validateInputsLiveData: MutableLiveData<Event<Resource<String>>> =
        MutableLiveData()
    val validateInputsLiveData: LiveData<Event<Resource<String>>> = _validateInputsLiveData

    private val _reAuthenticateUserLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val reAuthenticateUserLiveData: LiveData<Event<Resource<Nothing>>> = _reAuthenticateUserLiveData

    private val _changeUserPasswordLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val changeUserPasswordLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserPasswordLiveData

    fun validateChangePasswordInputs(
        currentPassword: String, newPassword: String, retypeNewPassword: String
    ) = viewModelScope.launch {
        safeValidateChangePasswordInputs(currentPassword, newPassword, retypeNewPassword)
    }

    private fun safeValidateChangePasswordInputs(
        currentPassword: String, newPassword: String, retypeNewPassword: String
    ) {
        if (currentPassword.isBlank()) {
            _validateInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_PASSWORD)))
            )
            return
        }

        if (newPassword.isBlank()) {
            _validateInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_NEW_PASSWORD)))
            )
            return
        }

        if (retypeNewPassword.isBlank()) {
            _validateInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_RETYPE_PASSWORD)))
            )
            return
        }

        if (UserHelper.passwordStrength(newPassword) == PASSWORD_STRENGTH_WEAK) {
            _validateInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_PASSWORD_SHORT)))
            )
            return
        }

        if (!UserHelper.isRetypePasswordValid(newPassword, retypeNewPassword)) {
            _validateInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_PASSWORD_NOT_MATCH)))
            )
            return
        }

        _validateInputsLiveData.postValue(Event(Resource.Success(currentPassword)))
    }

    fun reAuthenticateUser(password: String) = viewModelScope.launch {
        safeReAuthenticateUser(password)
    }

    private suspend fun safeReAuthenticateUser(password: String) {
        _reAuthenticateUserLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.reAuthenticateUser(password)
            _reAuthenticateUserLiveData.postValue(Event(Resource.Success()))
            Timber.i("User re-authenticated.")
        } catch (e: Exception) {
            Timber.e("safeReAuthenticateUser Exception: ${e.message}")
            _reAuthenticateUserLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun changeUserPassword(newPassword: String) = viewModelScope.launch {
        safeChangeUserPassword(newPassword)
    }

    private suspend fun safeChangeUserPassword(newPassword: String) {
        _changeUserPasswordLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.updateUserPassword(newPassword)
            _changeUserPasswordLiveData.postValue(Event(Resource.Success()))
            Timber.i("User password updated.")
        } catch (e: Exception) {
            Timber.e("safeChangeUserPassword Exception: ${e.message}")
            _changeUserPasswordLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}