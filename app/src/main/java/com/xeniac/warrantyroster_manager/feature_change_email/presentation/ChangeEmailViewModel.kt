package com.xeniac.warrantyroster_manager.feature_change_email.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.states.ChangeEmailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(ChangeEmailState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    fun onAction(action: ChangeEmailAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }
}