package com.xeniac.warrantyroster_manager.feature_auth.login.presentation

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
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases.LoginUseCases
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.states.LoginState
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.utils.asUiText
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
class LoginViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "loginState",
        initialValue = LoginState()
    )
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _loginWithEmailEventChannel = Channel<Event>()
    val loginWithEmailEventChannel = _loginWithEmailEventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> emailChanged(action.newValue)
            is LoginAction.PasswordChanged -> passwordChanged(action.newValue)
            LoginAction.LoginWithEmail -> loginWithEmail()
            LoginAction.LoginWithGoogle -> loginWithGoogle()
            LoginAction.LoginWithX -> loginWithX()
            LoginAction.LoginWithFacebook -> loginWithFacebook()
        }
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

    private fun loginWithEmail() {
        if (!hasNetworkConnection()) {
            _loginWithEmailEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        loginUseCases.loginWithEmailUseCase.get()(
            email = state.value.emailState.value.text,
            password = state.value.passwordState.value.text
        ).onStart {
            _state.update {
                it.copy(isLoginWithEmailLoading = true)
            }
        }.onEach { loginWithEmailResult ->
            loginWithEmailResult.emailError?.let { emailError ->
                _state.update {
                    it.copy(
                        emailState = it.emailState.copy(
                            errorText = emailError.asUiText()
                        )
                    )
                }
            }

            loginWithEmailResult.passwordError?.let { passwordError ->
                _state.update {
                    it.copy(
                        passwordState = it.passwordState.copy(
                            errorText = passwordError.asUiText()
                        )
                    )
                }
            }

            when (val result = loginWithEmailResult.result) {
                is Result.Success -> {
                    _loginWithEmailEventChannel.send(AuthUiEvent.NavigateToBaseScreen)
                }
                is Result.Error -> {
                    _loginWithEmailEventChannel.send(
                        UiEvent.ShowLongSnackbar(result.error.asUiText())
                    )
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isLoginWithEmailLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun loginWithGoogle() {

    }

    private fun loginWithX() {

    }

    private fun loginWithFacebook() {

    }
}