package com.xeniac.warrantyroster_manager.feature_onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_onboarding.domain.use_cases.OnboardingUseCases
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.states.OnboardingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingUseCases: OnboardingUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = combine(
        flow = _state,
        flow2 = onboardingUseCases.getCurrentAppLocaleUseCase.get()()
    ) { state, currentAppLocale ->
        _state.update {
            state.copy(currentAppLocale = currentAppLocale)
        }
        _state.value
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _setAppLocaleEventChannel = Channel<UiEvent>()
    val setAppLocaleEventChannel = _setAppLocaleEventChannel.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.ShowLocaleBottomSheet -> showLocaleBottomSheet()
            OnboardingAction.DismissLocaleBottomSheet -> dismissLocaleBottomSheet()
            is OnboardingAction.SetCurrentAppLocale -> setCurrentAppLocale(action.newAppLocale)
        }
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
            onboardingUseCases.storeCurrentAppLocaleUseCase.get()(
                newAppLocale = newAppLocale
            ).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _state.update {
                            it.copy(currentAppLocale = newAppLocale)
                        }

                        val isActivityRestartNeeded = result.data
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