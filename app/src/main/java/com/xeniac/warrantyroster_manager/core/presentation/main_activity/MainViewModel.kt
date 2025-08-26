package com.xeniac.warrantyroster_manager.core.presentation.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.use_cases.MainUseCases
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.OnboardingScreen
import com.xeniac.warrantyroster_manager.core.presentation.main_activity.states.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainUseCases: MainUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.onStart {
        getMainStateData()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 2.minutes),
        initialValue = _state.value
    )

    private fun getMainStateData() {
        mainUseCases.getCurrentAppLocaleUseCase.get()().onStart {
            _state.update {
                it.copy(isSplashScreenLoading = true)
            }
        }.zip(
            other = mainUseCases.getIsUserLoggedInUseCase.get()(),
            transform = { currentAppLocale, isUserLoggedIn ->
                _state.update {
                    it.copy(
                        currentAppLocale = currentAppLocale,
                        postSplashDestination = getPostSplashDestination(isUserLoggedIn)
                    )
                }
            }
        ).onCompletion {
            _state.update {
                it.copy(isSplashScreenLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun getPostSplashDestination(
        isUserLoggedIn: Boolean
    ): Any {
        return if (isUserLoggedIn) BaseScreen
        else OnboardingScreen
    }
}