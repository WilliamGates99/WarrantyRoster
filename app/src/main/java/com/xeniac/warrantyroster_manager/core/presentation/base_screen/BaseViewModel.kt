package com.xeniac.warrantyroster_manager.core.presentation.base_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.presentation.base_screen.states.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class BaseViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(BaseState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    fun onAction(action: BaseAction) {
//        when (action) {
//            else -> TODO("Handle actions")
//        }
    }
}