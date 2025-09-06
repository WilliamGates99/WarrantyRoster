package com.xeniac.warrantyroster_manager.feature_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.asUiText
import com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases.SettingsUseCases
import com.xeniac.warrantyroster_manager.feature_settings.presentation.states.SettingsState
import com.xeniac.warrantyroster_manager.feature_settings.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
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
class SettingsViewModel @Inject constructor(
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = combine(
        flow = _state,
        flow2 = settingsUseCases.getCurrentAppThemeUseCase.get()()
    ) { state, appTheme ->
        _state.update {
            state.copy(currentAppTheme = appTheme)
        }
        _state.value
    }.onStart {
        getCurrentAppLocaleUseCase()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _setAppLocaleEventChannel = Channel<UiEvent>()
    val setAppLocaleEventChannel = _setAppLocaleEventChannel.receiveAsFlow()

    private val _setAppThemeEventChannel = Channel<Event>()
    val setAppThemeEventChannel = _setAppThemeEventChannel.receiveAsFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.ShowLocaleBottomSheet -> showLocaleBottomSheet()
            SettingsAction.DismissLocaleBottomSheet -> dismissLocaleBottomSheet()
            SettingsAction.ShowThemeBottomSheet -> showThemeBottomSheet()
            SettingsAction.DismissThemeBottomSheet -> dismissThemeBottomSheet()
            is SettingsAction.SetCurrentAppLocale -> setCurrentAppLocale(action.newAppLocale)
            is SettingsAction.SetCurrentAppTheme -> setCurrentAppTheme(action.newAppTheme)
        }
    }

    private fun getCurrentAppLocaleUseCase() {
        settingsUseCases.getCurrentAppLocaleUseCase.get()().onEach { currentAppLocale ->
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

    private fun showThemeBottomSheet() = viewModelScope.launch {
        _state.update {
            it.copy(isThemeBottomSheetVisible = true)
        }
    }

    private fun dismissThemeBottomSheet() = viewModelScope.launch {
        _state.update {
            it.copy(isThemeBottomSheetVisible = false)
        }
    }

    private fun setCurrentAppLocale(
        newAppLocale: AppLocale
    ) {
        val shouldUpdateAppLocale = newAppLocale != _state.value.currentAppLocale
        if (shouldUpdateAppLocale) {
            settingsUseCases.storeCurrentAppLocaleUseCase.get()(
                newAppLocale = newAppLocale
            ).onEach { result ->
                when (result) {
                    is Result.Success -> result.data.let { isActivityRestartNeeded ->
                        _state.update {
                            it.copy(currentAppLocale = newAppLocale)
                        }

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

    private fun setCurrentAppTheme(
        newAppTheme: AppTheme
    ) {
        val shouldUpdateAppTheme = newAppTheme != _state.value.currentAppTheme
        if (shouldUpdateAppTheme) {
            settingsUseCases.storeCurrentAppThemeUseCase.get()(
                newAppTheme = newAppTheme
            ).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _setAppThemeEventChannel.send(SettingsUiEvent.UpdateAppTheme(newAppTheme))
                    }
                    is Result.Error -> {
                        _setAppThemeEventChannel.send(
                            UiEvent.ShowShortSnackbar(result.error.asUiText())
                        )
                    }
                }
            }.launchIn(scope = viewModelScope)
        }
    }
}