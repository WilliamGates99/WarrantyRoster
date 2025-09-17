package com.xeniac.warrantyroster_manager.feature_change_password.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ConfirmPasswordChecker
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.PasswordStrengthCalculator
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError
import com.xeniac.warrantyroster_manager.feature_change_password.domain.use_cases.ChangeUserPasswordUseCase
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.states.ChangePasswordState
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.utils.asUiText
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changeUserPasswordUseCase: Lazy<ChangeUserPasswordUseCase>,
    private val passwordStrengthCalculator: Lazy<PasswordStrengthCalculator>,
    private val confirmPasswordChecker: Lazy<ConfirmPasswordChecker>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "changePasswordState",
        initialValue = ChangePasswordState()
    )
    val state = _state.onStart {
        observeNewPasswordState()
        observeConfirmNewPasswordState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _changeUserPasswordEventChannel = Channel<UiEvent>()
    val changeUserPasswordEventChannel = _changeUserPasswordEventChannel.receiveAsFlow()

    fun onAction(action: ChangePasswordAction) {
        when (action) {
            ChangePasswordAction.DismissPasswordChangedDialog -> dismissPasswordChangedDialog()
            is ChangePasswordAction.CurrentPasswordChanged -> currentPasswordChanged(action.newValue)
            is ChangePasswordAction.NewPasswordChanged -> newPasswordChanged(action.newValue)
            is ChangePasswordAction.ConfirmNewPasswordChanged -> confirmNewPasswordChanged(action.newValue)
            ChangePasswordAction.ChangeUserPassword -> changeUserPassword()
        }
    }

    private fun observeNewPasswordState() {
        _state.distinctUntilChangedBy {
            it.newPasswordState
        }.mapLatest {
            passwordStrengthCalculator.get()(
                password = it.newPasswordState.value.text
            )
        }.onEach { newPasswordStrength ->
            _state.update {
                it.copy(
                    newPasswordState = it.newPasswordState.copy(
                        strength = newPasswordStrength
                    )
                )
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun observeConfirmNewPasswordState() {
        _state.distinctUntilChangedBy {
            it.confirmNewPasswordState
        }.mapLatest {
            confirmPasswordChecker.get()(
                password = it.newPasswordState.value.text,
                confirmPassword = it.confirmNewPasswordState.value.text
            )
        }.onEach { passwordMatchingState ->
            _state.update {
                it.copy(
                    confirmNewPasswordState = it.confirmNewPasswordState.copy(
                        passwordMatchingState = passwordMatchingState
                    )
                )
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun dismissPasswordChangedDialog() = viewModelScope.launch {
        _state.update {
            it.copy(isPasswordChangedDialogVisible = false)
        }
    }

    private fun currentPasswordChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                currentPasswordState = it.currentPasswordState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun newPasswordChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                newPasswordState = it.newPasswordState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun confirmNewPasswordChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                confirmNewPasswordState = it.confirmNewPasswordState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun changeUserPassword() {
        if (!hasNetworkConnection()) {
            _changeUserPasswordEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        changeUserPasswordUseCase.get()(
            currentPassword = _state.value.currentPasswordState.value.text,
            newPassword = _state.value.newPasswordState.value.text,
            confirmNewPassword = _state.value.confirmNewPasswordState.value.text
        ).onStart {
            _state.update {
                it.copy(isChangeUserPasswordLoading = true)
            }
        }.onEach { changeUserPasswordResult ->
            changeUserPasswordResult.currentPasswordError?.let { currentPasswordError ->
                _state.update {
                    it.copy(
                        currentPasswordState = it.currentPasswordState.copy(
                            errorText = currentPasswordError.asUiText()
                        )
                    )
                }
            }

            changeUserPasswordResult.newPasswordError?.let { newPasswordError ->
                _state.update {
                    it.copy(
                        newPasswordState = it.newPasswordState.copy(
                            errorText = newPasswordError.asUiText()
                        )
                    )
                }
            }

            changeUserPasswordResult.confirmNewPasswordError?.let { confirmNewPasswordError ->
                _state.update {
                    it.copy(
                        confirmNewPasswordState = it.confirmNewPasswordState.copy(
                            errorText = confirmNewPasswordError.asUiText()
                        )
                    )
                }
            }

            when (val result = changeUserPasswordResult.result) {
                is Result.Success -> {
                    _state.update { it.copy(isPasswordChangedDialogVisible = true) }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        ChangeUserPasswordError.Network.FirebaseAuthUnauthorizedUser -> {
                            _changeUserPasswordEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                            _changeUserPasswordEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _changeUserPasswordEventChannel.send(
                                UiEvent.ShowLongSnackbar(error.asUiText())
                            )
                        }
                    }
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isChangeUserPasswordLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}