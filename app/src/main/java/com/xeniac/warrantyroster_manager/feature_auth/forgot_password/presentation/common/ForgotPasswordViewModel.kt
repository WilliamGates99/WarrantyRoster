package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases.ForgotPasswordUseCases
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.states.ForgotPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCases: ForgotPasswordUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }
}