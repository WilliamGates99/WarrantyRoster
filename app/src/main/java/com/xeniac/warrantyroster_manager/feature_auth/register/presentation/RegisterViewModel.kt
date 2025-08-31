package com.xeniac.warrantyroster_manager.feature_auth.register.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthUiEvent
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases.RegisterUseCases
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.states.RegisterState
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.utils.asUiText
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
class RegisterViewModel @Inject constructor(
    private val registerUseCases: RegisterUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "registerState",
        initialValue = RegisterState()
    )
    val state = _state.onStart {
        observePasswordState()
        observeConfirmPasswordState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _registerWithEmailEventChannel = Channel<Event>()
    val registerWithEmailEventChannel = _registerWithEmailEventChannel.receiveAsFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.EmailChanged -> emailChanged(action.newValue)
            is RegisterAction.PasswordChanged -> passwordChanged(action.newValue)
            is RegisterAction.ConfirmPasswordChanged -> confirmPasswordChanged(action.newValue)
            RegisterAction.RegisterWithEmail -> registerWithEmail()
        }
    }

    private fun observePasswordState() {

    }

    private fun observeConfirmPasswordState() {

    }

    private fun emailChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                emailState = it.emailState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
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

    private fun confirmPasswordChanged(
        newValue: TextFieldValue
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                confirmPasswordState = it.confirmPasswordState.copy(
                    value = newValue.copy(
                        text = newValue.text.convertDigitsToEnglish()
                    ),
                    errorText = null
                )
            )
        }
    }

    private fun registerWithEmail() {
        if (!hasNetworkConnection()) {
            _registerWithEmailEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        registerUseCases.registerWithEmailUseCase.get()(
            email = state.value.emailState.value.text,
            password = state.value.passwordState.value.text,
            confirmPassword = state.value.confirmPasswordState.value.text
        ).onStart {
            _state.update {
                it.copy(isRegisterWithEmailLoading = true)
            }
        }.onEach { registerWithEmailResult ->
            registerWithEmailResult.emailError?.let { emailError ->
                _state.update {
                    it.copy(
                        emailState = it.emailState.copy(
                            errorText = emailError.asUiText()
                        )
                    )
                }
            }

            registerWithEmailResult.passwordError?.let { passwordError ->
                _state.update {
                    it.copy(
                        passwordState = it.passwordState.copy(
                            errorText = passwordError.asUiText()
                        )
                    )
                }
            }

            registerWithEmailResult.confirmPasswordError?.let { confirmPasswordError ->
                _state.update {
                    it.copy(
                        confirmPasswordState = it.confirmPasswordState.copy(
                            errorText = confirmPasswordError.asUiText()
                        )
                    )
                }
            }

            when (val result = registerWithEmailResult.result) {
                is Result.Success -> {
                    _registerWithEmailEventChannel.send(AuthUiEvent.NavigateToBaseScreen)
                }
                is Result.Error -> {
                    _registerWithEmailEventChannel.send(
                        UiEvent.ShowLongSnackbar(result.error.asUiText())
                    )
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isRegisterWithEmailLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}