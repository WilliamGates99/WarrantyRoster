package com.xeniac.warrantyroster_manager.feature_auth.login.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.credentials.Credential
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthUiEvent
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases.LoginUseCases
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.states.LoginState
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.utils.asUiText
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
class LoginViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    val xOAuthProvider: Lazy<OAuthProvider>,
    val firebaseAuth: Lazy<FirebaseAuth>,
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

    private val _loginWithGoogleEventChannel = Channel<Event>()
    val loginWithGoogleEventChannel = _loginWithGoogleEventChannel.receiveAsFlow()

    private val _loginWithXEventChannel = Channel<Event>()
    val loginWithXEventChannel = _loginWithXEventChannel.receiveAsFlow()

    private val _loginWithFacebookEventChannel = Channel<Event>()
    val loginWithFacebookEventChannel = _loginWithFacebookEventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> emailChanged(action.newValue)
            is LoginAction.PasswordChanged -> passwordChanged(action.newValue)
            LoginAction.LoginWithEmail -> loginWithEmail()
            LoginAction.LoginWithGoogle -> getGoogleCredential()
            LoginAction.CheckPendingLoginWithX -> checkPendingLoginWithX()
            is LoginAction.LoginWithX -> loginWithX(action.loginWithXTask)
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

    private fun getGoogleCredential() {
        if (!hasNetworkConnection()) {
            _loginWithGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        loginUseCases.getGoogleCredentialUseCase.get()().onStart {
            _state.update {
                it.copy(isLoginWithGoogleLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> loginWithGoogle(credential = result.data)
                is Result.Error -> {
                    _state.update {
                        it.copy(isLoginWithGoogleLoading = false)
                    }

                    when (val error = result.error) {
                        GetGoogleCredentialError.CancellationException -> Unit
                        else -> _loginWithGoogleEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                    }
                }
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun loginWithGoogle(
        credential: Credential
    ) {
        if (!hasNetworkConnection()) {
            _loginWithGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            _state.update {
                it.copy(isLoginWithGoogleLoading = false)
            }
            return
        }

        loginUseCases.loginWithGoogleUseCase.get()(
            credential = credential
        ).onStart {
            _state.update {
                it.copy(isLoginWithGoogleLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _loginWithGoogleEventChannel.send(AuthUiEvent.NavigateToBaseScreen)
                }
                is Result.Error -> {
                    _loginWithGoogleEventChannel.send(
                        UiEvent.ShowLongSnackbar(result.error.asUiText())
                    )
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isLoginWithGoogleLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun checkPendingLoginWithX() {
        if (!hasNetworkConnection()) {
            _loginWithXEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        loginUseCases.checkPendingLoginWithXUseCase.get()().onStart {
            _state.update {
                it.copy(isLoginWithXLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    val pendingLoginWithXTask = result.data
                    when {
                        pendingLoginWithXTask != null -> { // Handle pending login
                            loginWithX(loginWithXTask = pendingLoginWithXTask)
                        }
                        else -> { // Start new login activity
                            _loginWithXEventChannel.send(AuthUiEvent.StartActivityForLoginWithX)
                        }
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(isLoginWithXLoading = false)
                    }
                    _loginWithXEventChannel.send(UiEvent.ShowLongSnackbar(result.error.asUiText()))
                }
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun loginWithX(
        loginWithXTask: Task<AuthResult>
    ) {
        if (!hasNetworkConnection()) {
            _loginWithXEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            _state.update {
                it.copy(isLoginWithXLoading = false)
            }
            return
        }

        loginUseCases.loginWithXUseCase.get()(
            loginWithXTask = loginWithXTask
        ).onStart {
            _state.update {
                it.copy(isLoginWithXLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _loginWithXEventChannel.send(AuthUiEvent.NavigateToBaseScreen)
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        LoginWithXError.CancellationException -> Unit
                        else -> _loginWithXEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isLoginWithXLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun loginWithFacebook() {
        if (!hasNetworkConnection()) {
            _loginWithFacebookEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        loginUseCases.loginWithFacebookUseCase.get()().onStart {
            _state.update {
                it.copy(isLoginWithFacebookLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _loginWithFacebookEventChannel.send(AuthUiEvent.NavigateToBaseScreen)
                }
                is Result.Error -> {
                    _loginWithFacebookEventChannel.send(
                        UiEvent.ShowLongSnackbar(result.error.asUiText())
                    )
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isLoginWithFacebookLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}