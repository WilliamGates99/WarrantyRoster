package com.xeniac.warrantyroster_manager.settings.presentation.change_email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_EMAIL_SAME
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.util.UserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _checkInputsLiveData: MutableLiveData<Event<Resource<String>>> = MutableLiveData()
    val checkInputsLiveData: LiveData<Event<Resource<String>>> = _checkInputsLiveData

    private val _reAuthenticateUserLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val reAuthenticateUserLiveData: LiveData<Event<Resource<Nothing>>> = _reAuthenticateUserLiveData

    private val _changeUserEmailLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val changeUserEmailLiveData: LiveData<Event<Resource<Nothing>>> = _changeUserEmailLiveData

    fun validateChangeEmailInputs(
        password: String,
        newEmail: String,
        currentUserEmail: String = userRepository.getCurrentUserEmail()
    ) = viewModelScope.launch {
        safeValidateChangeEmailInputs(password, newEmail, currentUserEmail)
    }

    private fun safeValidateChangeEmailInputs(
        password: String,
        newEmail: String,
        currentUserEmail: String = userRepository.getCurrentUserEmail()
    ) {
        if (password.isBlank()) {
            _checkInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_PASSWORD)))
            )
            return
        }

        if (newEmail.isBlank()) {
            _checkInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EMAIL)))
            )
            return
        }

        if (!UserHelper.isEmailValid(newEmail)) {
            _checkInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_EMAIL_INVALID)))
            )
            return
        }

        if (newEmail == currentUserEmail) {
            _checkInputsLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_EMAIL_SAME)))
            )
            return
        }

        _checkInputsLiveData.postValue(Event(Resource.Success(password)))
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

    fun changeUserEmail(newEmail: String) = viewModelScope.launch {
        safeChangeUserEmail(newEmail)
    }

    private suspend fun safeChangeUserEmail(newEmail: String) {
        _changeUserEmailLiveData.postValue(Event(Resource.Loading()))
        try {
            userRepository.updateUserEmail(newEmail)
            _changeUserEmailLiveData.postValue(Event(Resource.Success()))
            Timber.i("User email updated to ${newEmail}.")
        } catch (e: Exception) {
            Timber.e("safeChangeUserEmail Exception: ${e.message}")
            _changeUserEmailLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}