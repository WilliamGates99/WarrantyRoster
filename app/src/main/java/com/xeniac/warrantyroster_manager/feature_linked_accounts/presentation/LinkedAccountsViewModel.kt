package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.states.LinkedAccountsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LinkedAccountsViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(LinkedAccountsState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
        initialValue = _state.value
    )

    fun onAction(action: LinkedAccountsAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

    /**
     * const val ERROR_FIREBASE_AUTH_ALREADY_LINKED = "User has already been linked to the given provider"
     */
}