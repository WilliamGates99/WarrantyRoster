package com.xeniac.warrantyroster_manager.feature_auth.register.presentation

import androidx.compose.ui.text.input.TextFieldValue
import androidx.credentials.Credential
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.core.di.GithubQualifier
import com.xeniac.warrantyroster_manager.core.di.XQualifier
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ConfirmPasswordChecker
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.PasswordStrengthCalculator
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthUiEvent
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases.RegisterUseCases
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.states.RegisterState
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.utils.asUiText
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
class RegisterViewModel @Inject constructor(
    private val registerUseCases: RegisterUseCases,
    private val passwordStrengthCalculator: Lazy<PasswordStrengthCalculator>,
    private val confirmPasswordChecker: Lazy<ConfirmPasswordChecker>,
    @XQualifier val xOAuthProvider: Lazy<OAuthProvider>,
    @GithubQualifier val githubOAuthProvider: Lazy<OAuthProvider>,
    val firebaseAuth: Lazy<FirebaseAuth>,
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

    private val _loginWithGoogleEventChannel = Channel<Event>()
    val loginWithGoogleEventChannel = _loginWithGoogleEventChannel.receiveAsFlow()

    private val _loginWithXEventChannel = Channel<Event>()
    val loginWithXEventChannel = _loginWithXEventChannel.receiveAsFlow()

    private val _loginWithGithubEventChannel = Channel<Event>()
    val loginWithGithubEventChannel = _loginWithGithubEventChannel.receiveAsFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.EmailChanged -> emailChanged(action.newValue)
            is RegisterAction.PasswordChanged -> passwordChanged(action.newValue)
            is RegisterAction.ConfirmPasswordChanged -> confirmPasswordChanged(action.newValue)
            RegisterAction.RegisterWithEmail -> registerWithEmail()
            RegisterAction.LoginWithGoogle -> getGoogleCredential()
            RegisterAction.CheckPendingLoginWithX -> checkPendingLoginWithX()
            is RegisterAction.LoginWithX -> loginWithX(action.loginWithXTask)
            RegisterAction.CheckPendingLoginWithGithub -> checkPendingLoginWithGithub()
            is RegisterAction.LoginWithGithub -> loginWithGithub(action.loginWithGithubTask)
        }
    }

    private fun observePasswordState() {
        _state.distinctUntilChangedBy {
            it.passwordState
        }.mapLatest {
            passwordStrengthCalculator.get()(
                password = it.passwordState.value.text
            )
        }.onEach { passwordStrength ->
            _state.update {
                it.copy(
                    passwordState = it.passwordState.copy(
                        strength = passwordStrength
                    )
                )
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun observeConfirmPasswordState() {
        _state.distinctUntilChangedBy {
            it.confirmPasswordState
        }.mapLatest {
            confirmPasswordChecker.get()(
                password = it.passwordState.value.text,
                confirmPassword = it.confirmPasswordState.value.text
            )
        }.onEach { passwordMatchingState ->
            _state.update {
                it.copy(
                    confirmPasswordState = it.confirmPasswordState.copy(
                        passwordMatchingState = passwordMatchingState
                    )
                )
            }
        }.launchIn(scope = viewModelScope)
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
            email = _state.value.emailState.value.text,
            password = _state.value.passwordState.value.text,
            confirmPassword = _state.value.confirmPasswordState.value.text
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

    private fun getGoogleCredential() {
        if (!hasNetworkConnection()) {
            _loginWithGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        registerUseCases.getGoogleCredentialUseCase.get()().onStart {
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

        registerUseCases.loginWithGoogleUseCase.get()(
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

        registerUseCases.checkPendingLoginWithXUseCase.get()().onStart {
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

        registerUseCases.loginWithXUseCase.get()(
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

    private fun checkPendingLoginWithGithub() {
        if (!hasNetworkConnection()) {
            _loginWithGithubEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        registerUseCases.checkPendingLoginWithGithubUseCase.get()().onStart {
            _state.update {
                it.copy(isLoginWithGithubLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    val pendingLoginWithGithubTask = result.data
                    when {
                        pendingLoginWithGithubTask != null -> { // Handle pending login
                            loginWithGithub(loginWithGithubTask = pendingLoginWithGithubTask)
                        }
                        else -> { // Start new login activity
                            _loginWithGithubEventChannel.send(AuthUiEvent.StartActivityForLoginWithGithub)
                        }
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(isLoginWithGithubLoading = false)
                    }
                    _loginWithGithubEventChannel.send(UiEvent.ShowLongSnackbar(result.error.asUiText()))
                }
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun loginWithGithub(
        loginWithGithubTask: Task<AuthResult>
    ) {
        if (!hasNetworkConnection()) {
            _loginWithGithubEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            _state.update {
                it.copy(isLoginWithGithubLoading = false)
            }
            return
        }

        registerUseCases.loginWithGithubUseCase.get()(
            loginWithGithubTask = loginWithGithubTask
        ).onStart {
            _state.update {
                it.copy(isLoginWithGithubLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _loginWithGithubEventChannel.send(AuthUiEvent.NavigateToBaseScreen)
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        LoginWithGithubError.CancellationException -> Unit
                        else -> _loginWithGithubEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isLoginWithGithubLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}