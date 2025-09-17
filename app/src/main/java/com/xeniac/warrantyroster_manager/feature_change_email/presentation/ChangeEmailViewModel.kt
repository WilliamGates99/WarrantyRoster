package com.xeniac.warrantyroster_manager.feature_change_email.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError
import com.xeniac.warrantyroster_manager.feature_change_email.domain.use_cases.ChangeUserEmailUseCase
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.states.ChangeEmailState
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.utils.asUiText
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val changeUserEmailUseCase: Lazy<ChangeUserEmailUseCase>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "changeEmailState",
        initialValue = ChangeEmailState()
    )
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _changeUserEmailEventChannel = Channel<UiEvent>()
    val changeUserEmailEventChannel = _changeUserEmailEventChannel.receiveAsFlow()

    fun onAction(action: ChangeEmailAction) {
        when (action) {
            ChangeEmailAction.DismissVerificationEmailSentDialog -> dismissVerificationEmailSentDialog()
            is ChangeEmailAction.PasswordChanged -> passwordChanged(action.newValue)
            is ChangeEmailAction.NewEmailChanged -> newEmailChanged(action.newValue)
            ChangeEmailAction.ChangeUserEmail -> changeUserEmail()
        }
    }

    private fun dismissVerificationEmailSentDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isVerificationEmailSentDialogVisible = false)
        }
    }

    private fun passwordChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                passwordState = it.passwordState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun newEmailChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                newEmailState = it.newEmailState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun changeUserEmail() {
        if (!hasNetworkConnection()) {
            _changeUserEmailEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        changeUserEmailUseCase.get()(
            password = _state.value.passwordState.value.text,
            newEmail = _state.value.newEmailState.value.text
        ).onStart {
            _state.update {
                it.copy(isChangeUserEmailLoading = true)
            }
        }.onEach { changeUserEmailResult ->
            changeUserEmailResult.passwordError?.let { passwordError ->
                _state.update {
                    it.copy(
                        passwordState = it.passwordState.copy(
                            errorText = passwordError.asUiText()
                        )
                    )
                }
            }

            changeUserEmailResult.newEmailError?.let { newEmailError ->
                _state.update {
                    it.copy(
                        newEmailState = it.newEmailState.copy(
                            errorText = newEmailError.asUiText()
                        )
                    )
                }
            }

            when (val result = changeUserEmailResult.result) {
                is Result.Success -> {
                    _state.update { it.copy(isVerificationEmailSentDialogVisible = true) }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        ChangeUserEmailError.Network.FirebaseAuthUnauthorizedUser -> {
                            _changeUserEmailEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                            _changeUserEmailEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _changeUserEmailEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                        }
                    }
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isChangeUserEmailLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}