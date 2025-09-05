package com.xeniac.warrantyroster_manager.core.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.use_cases.UserUseCases
import com.xeniac.warrantyroster_manager.core.presentation.common.states.UserProfileState
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val userState = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _logoutEventChannel = Channel<UiEvent>()
    val logoutEventChannel = _logoutEventChannel.receiveAsFlow()

    fun onAction(action: UserAction) {
        when (action) {
            UserAction.Logout -> logout()
            UserAction.ForceLogoutUnauthorizedUser -> forceLogoutUnauthorizedUser()
        }
    }

    private fun logout() {
        userUseCases.logoutUserUseCase.get()().onStart {
            _state.update {
                it.copy(isLogoutLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _logoutEventChannel.send(UiEvent.ClearCoilCache)
                    _logoutEventChannel.send(UiEvent.NavigateToAuthScreen)
                }
                is Result.Error -> forceLogoutUnauthorizedUser()
            }
        }.onCompletion {
            _state.update {
                it.copy(isLogoutLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun forceLogoutUnauthorizedUser() {
        userUseCases.forceLogoutUnauthorizedUserUseCase.get()().onStart {
            _state.update {
                it.copy(isLogoutLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _logoutEventChannel.send(UiEvent.ClearCoilCache)
                    _logoutEventChannel.send(UiEvent.NavigateToAuthScreen)
                }
                is Result.Error -> {
                    _logoutEventChannel.send(UiEvent.ShowLongSnackbar(result.error.asUiText()))
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isLogoutLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}