package com.xeniac.warrantyroster_manager.feature_auth.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.AuthUseCases
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.states.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.onStart {
        getCurrentAppLocaleUseCase()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _setAppLocaleEventChannel = Channel<UiEvent>()
    val setAppLocaleEventChannel = _setAppLocaleEventChannel.receiveAsFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            AuthAction.ShowLocaleBottomSheet -> showLocaleBottomSheet()
            AuthAction.DismissLocaleBottomSheet -> dismissLocaleBottomSheet()
            is AuthAction.SetCurrentAppLocale -> setCurrentAppLocale(action.newAppLocale)
        }
    }

    private fun getCurrentAppLocaleUseCase() {
        authUseCases.getCurrentAppLocaleUseCase.get()().onEach { currentAppLocale ->
            _state.update {
                it.copy(currentAppLocale = currentAppLocale)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun showLocaleBottomSheet() = viewModelScope.launch {
        _state.update {
            it.copy(isLocaleBottomSheetVisible = true)
        }
    }

    private fun dismissLocaleBottomSheet() = viewModelScope.launch {
        _state.update {
            it.copy(isLocaleBottomSheetVisible = false)
        }
    }

    private fun setCurrentAppLocale(
        newAppLocale: AppLocale
    ) {
        val shouldUpdateAppLocale = newAppLocale != _state.value.currentAppLocale
        if (shouldUpdateAppLocale) {
            authUseCases.storeCurrentAppLocaleUseCase.get()(
                newAppLocale = newAppLocale
            ).onStart {
                _state.update {
                    it.copy(currentAppLocale = newAppLocale)
                }
            }.onEach { result ->
                when (result) {
                    is Result.Success -> result.data.let { isActivityRestartNeeded ->
                        if (isActivityRestartNeeded) {
                            _setAppLocaleEventChannel.send(UiEvent.RestartActivity)
                        }
                    }
                    is Result.Error -> {
                        _setAppLocaleEventChannel.send(
                            UiEvent.ShowShortSnackbar(result.error.asUiText())
                        )
                    }
                }
            }.launchIn(scope = viewModelScope)
        }
    }
}