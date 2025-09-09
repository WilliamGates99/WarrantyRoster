package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkedAccountsUseCases
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.states.LinkedAccountsState
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LinkedAccountsViewModel @Inject constructor(
    private val linkedAccountsUseCases: LinkedAccountsUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LinkedAccountsState())
    val state = _state.onStart {
        getLinkedAccountProviders()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeout = 30.seconds),
        initialValue = _state.value
    )

    private val _getLinkedAccountProvidersEventChannel = Channel<UiEvent>()
    val getLinkedAccountProvidersEventChannel =
        _getLinkedAccountProvidersEventChannel.receiveAsFlow()

    fun onAction(action: LinkedAccountsAction) {
        when (action) {
            LinkedAccountsAction.GetLinkedAccountProviders -> getLinkedAccountProviders()
        }
    }

    private fun getLinkedAccountProviders() {
        linkedAccountsUseCases.getLinkedAccountProvidersUseCase.get()().onStart {
            _state.update {
                it.copy(isLinkedAccountProvidersLoading = true)
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    val linkedAccountProviders = result.data

                    _state.update {
                        it.copy(
                            accountProviders = it.accountProviders.map { accountProvider ->
                                if (accountProvider.accountProvider in linkedAccountProviders) {
                                    accountProvider.copy(isConnected = true)
                                } else accountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        GetLinkedAccountProvidersError.Network.FirebaseAuthUnauthorizedUser -> {
                            _getLinkedAccountProvidersEventChannel.send(
                                UiEvent.ForceLogoutUnauthorizedUser
                            )
                        }
                        else -> {
                            _state.update {
                                it.copy(errorMessage = error.asUiText())
                            }
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(isLinkedAccountProvidersLoading = false)
            }
        }.launchIn(scope = viewModelScope)
    }
}