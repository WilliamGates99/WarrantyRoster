package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders
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

    private val _linkGoogleEventChannel = Channel<UiEvent>()
    val linkGoogleEventChannel = _linkGoogleEventChannel.receiveAsFlow()

    private val _linkXEventChannel = Channel<UiEvent>()
    val linkXEventChannel = _linkXEventChannel.receiveAsFlow()

    private val _linkGithubEventChannel = Channel<UiEvent>()
    val linkGithubEventChannel = _linkGithubEventChannel.receiveAsFlow()

    private val _unlinkGoogleEventChannel = Channel<UiEvent>()
    val unlinkGoogleEventChannel = _unlinkGoogleEventChannel.receiveAsFlow()

    private val _unlinkXEventChannel = Channel<UiEvent>()
    val unlinkXEventChannel = _unlinkXEventChannel.receiveAsFlow()

    private val _unlinkGithubEventChannel = Channel<UiEvent>()
    val unlinkGithubEventChannel = _unlinkGithubEventChannel.receiveAsFlow()

    fun onAction(action: LinkedAccountsAction) {
        when (action) {
            LinkedAccountsAction.GetLinkedAccountProviders -> getLinkedAccountProviders()
            LinkedAccountsAction.LinkGoogleAccount -> linkGoogleAccount()
            LinkedAccountsAction.LinkXAccount -> linkXAccount()
            LinkedAccountsAction.LinkGithubAccount -> linkGithubAccount()
            LinkedAccountsAction.UnlinkGoogleAccount -> unlinkGoogleAccount()
            LinkedAccountsAction.UnlinkXAccount -> unlinkXAccount()
            LinkedAccountsAction.UnlinkGithubAccount -> unlinkGithubAccount()
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
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider in linkedAccountProviders) {
                                    uiAccountProvider.copy(isLinked = true)
                                } else uiAccountProvider
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

    private fun linkGoogleAccount() {
        if (!hasNetworkConnection()) {
            _linkGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        // TODO: IMPLEMENT
    }

    private fun linkXAccount() {

    }

    private fun linkGithubAccount() {

    }

    private fun unlinkGoogleAccount() {
        if (!hasNetworkConnection()) {
            _unlinkGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        linkedAccountsUseCases.unlinkGoogleAccountUseCase.get()().onStart {
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.GOOGLE) {
                            uiAccountProvider.copy(isLoading = true)
                        } else uiAccountProvider
                    }
                )
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider == AccountProviders.GOOGLE) {
                                    uiAccountProvider.copy(isLinked = false)
                                } else uiAccountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        UnlinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser -> {
                            _unlinkGoogleEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _unlinkGoogleEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.GOOGLE) {
                            uiAccountProvider.copy(isLoading = false)
                        } else uiAccountProvider
                    }
                )
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun unlinkXAccount() {
        if (!hasNetworkConnection()) {
            _unlinkXEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        linkedAccountsUseCases.unlinkXAccountUseCase.get()().onStart {
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.X) {
                            uiAccountProvider.copy(isLoading = true)
                        } else uiAccountProvider
                    }
                )
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider == AccountProviders.X) {
                                    uiAccountProvider.copy(isLinked = false)
                                } else uiAccountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        UnlinkXAccountError.Network.FirebaseAuthUnauthorizedUser -> {
                            _unlinkXEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _unlinkXEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.X) {
                            uiAccountProvider.copy(isLoading = false)
                        } else uiAccountProvider
                    }
                )
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun unlinkGithubAccount() {
        if (!hasNetworkConnection()) {
            _unlinkGithubEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        linkedAccountsUseCases.unlinkGoogleAccountUseCase.get()().onStart {
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.GITHUB) {
                            uiAccountProvider.copy(isLoading = true)
                        } else uiAccountProvider
                    }
                )
            }
        }.onEach { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider == AccountProviders.GITHUB) {
                                    uiAccountProvider.copy(isLinked = false)
                                } else uiAccountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        UnlinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser -> {
                            _unlinkGithubEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _unlinkGithubEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                        }
                    }
                }
            }
        }.onCompletion {
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.GITHUB) {
                            uiAccountProvider.copy(isLoading = false)
                        } else uiAccountProvider
                    }
                )
            }
        }.launchIn(scope = viewModelScope)
    }
}