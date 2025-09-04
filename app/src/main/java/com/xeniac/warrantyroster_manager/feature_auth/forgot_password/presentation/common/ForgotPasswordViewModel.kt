package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.utils.convertDigitsToEnglish
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases.ForgotPasswordUseCases
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.states.ForgotPasswordState
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
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
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCases: ForgotPasswordUseCases,
    private val decimalFormat: DecimalFormat,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = savedStateHandle.getMutableStateFlow(
        key = "forgotPasswordState",
        initialValue = ForgotPasswordState()
    )
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _state.value
    )

    private val _timerValueInSeconds = MutableStateFlow(0)

    private val _timerText = MutableStateFlow(
        UiText.StringResource(
            resId = R.string.forgot_pw_sent_text_timer,
            decimalFormat.format(0),
            decimalFormat.format(0)
        )
    )
    val timerText = _timerText.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _timerText.value
    )

    private val _sendResetPasswordEmailEventChannel = Channel<Event>()
    val sendResetPasswordEmailEventChannel = _sendResetPasswordEmailEventChannel.receiveAsFlow()

    private var countDownTimerJob: Job? = null

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.EmailChanged -> emailChanged(action.newValue)
            ForgotPasswordAction.SendResetPasswordEmail -> sendResetPasswordEmail()
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

    private fun sendResetPasswordEmail() {
        if (!hasNetworkConnection()) {
            _sendResetPasswordEmailEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        forgotPasswordUseCases.sendResetPasswordEmailUseCase.get()(
            email = _state.value.emailState.value.text,
            timerValueInSeconds = _timerValueInSeconds.value
        ).onStart {
            _state.update {
                it.copy(isSendResetPasswordEmailLoading = true)
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

            when (val result = loginWithEmailResult.result) {
                is Result.Success -> {
                    startCountDownTimer()
                    _state.update {
                        it.copy(
                            sentResetPasswordEmailsCount = it.sentResetPasswordEmailsCount.plus(1)
                        )
                    }
                    _sendResetPasswordEmailEventChannel.send(
                        ForgotPasswordUiEvent.NavigateToResetPwInstructionScreen
                    )
                }
                is Result.Error -> {
                    _sendResetPasswordEmailEventChannel.send(
                        UiEvent.ShowLongSnackbar(result.error.asUiText())
                    )
                }
                null -> Unit
            }
        }.onCompletion {
            _state.update {
                it.copy(isSendResetPasswordEmailLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun startCountDownTimer() {
        countDownTimerJob?.cancel()

        countDownTimerJob = forgotPasswordUseCases.observeCountDownTimerUseCase.get()().onStart {
            _state.update {
                it.copy(isTimerTicking = true)
            }
        }.onEach { timerValueInSeconds ->
            _timerValueInSeconds.update { timerValueInSeconds }

            val isTimerFinished = timerValueInSeconds == 0
            if (isTimerFinished) {
                _state.update {
                    it.copy(isTimerTicking = false)
                }
                return@onEach
            }

            _timerText.update {
                val minutes = decimalFormat.format(timerValueInSeconds / 60)
                val seconds = decimalFormat.format(timerValueInSeconds % 60)

                UiText.StringResource(
                    resId = R.string.forgot_pw_sent_text_timer,
                    minutes,
                    seconds
                )
            }
        }.onCompletion {
            _state.update {
                it.copy(isTimerTicking = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}