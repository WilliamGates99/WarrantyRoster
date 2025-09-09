package com.xeniac.warrantyroster_manager.feature_change_password.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.feature_change_password.domain.use_cases.ChangeUserPasswordUseCase
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.states.ChangePasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changeUserPasswordUseCase: Lazy<ChangeUserPasswordUseCase>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    fun onAction(action: ChangePasswordAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }
}