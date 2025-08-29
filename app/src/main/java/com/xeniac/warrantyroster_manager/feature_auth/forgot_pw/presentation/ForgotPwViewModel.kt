package com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation.states.ForgotPwState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ForgotPwViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPwState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    fun onAction(action: ForgotPwAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }
}