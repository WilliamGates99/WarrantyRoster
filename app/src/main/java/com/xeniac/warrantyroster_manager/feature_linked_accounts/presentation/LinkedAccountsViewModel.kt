package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.core.di.GithubQualifier
import com.xeniac.warrantyroster_manager.core.di.XQualifier
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.NetworkObserverHelper.hasNetworkConnection
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases.LinkedAccountsUseCases
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.states.LinkedAccountsState
import com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils.asUiText
import dagger.Lazy
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
    private val linkedAccountsUseCases: LinkedAccountsUseCases,
    @XQualifier val xOAuthProvider: Lazy<OAuthProvider>,
    @GithubQualifier val githubOAuthProvider: Lazy<OAuthProvider>,
    val firebaseAuth: Lazy<FirebaseAuth>,
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

    private val _linkXEventChannel = Channel<Event>()
    val linkXEventChannel = _linkXEventChannel.receiveAsFlow()

    private val _linkGithubEventChannel = Channel<Event>()
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
            LinkedAccountsAction.LinkGoogleAccount -> getGoogleCredential()
            LinkedAccountsAction.CheckPendingLinkXAccount -> checkPendingLinkXAccount()
            is LinkedAccountsAction.LinkXAccount -> linkXAccount(action.linkXAccountTask)
            LinkedAccountsAction.CheckPendingLinkGithubAccount -> checkPendingLinkGithubAccount()
            is LinkedAccountsAction.LinkGithubAccount -> linkGithubAccount(action.linkGithubAccountTask)
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

    private fun getGoogleCredential() {
        if (!hasNetworkConnection()) {
            _linkGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        linkedAccountsUseCases.getGoogleCredentialUseCase.get()().onStart {
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
                is Result.Success -> linkGoogleAccount(credential = result.data)
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider == AccountProviders.GOOGLE) {
                                    uiAccountProvider.copy(isLoading = false)
                                } else uiAccountProvider
                            }
                        )
                    }

                    when (val error = result.error) {
                        GetGoogleCredentialError.CancellationException -> Unit
                        else -> _linkGoogleEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
                    }
                }
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun linkGoogleAccount(
        credential: Credential
    ) {
        if (!hasNetworkConnection()) {
            _linkGoogleEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.GOOGLE) {
                            uiAccountProvider.copy(isLoading = false)
                        } else uiAccountProvider
                    }
                )
            }
            return
        }

        linkedAccountsUseCases.linkGoogleAccountUseCase.get()(
            credential = credential
        ).onStart {
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
                                    uiAccountProvider.copy(isLinked = true)
                                } else uiAccountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        LinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser -> {
                            _linkGoogleEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> {
                            _linkGoogleEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
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

    private fun checkPendingLinkXAccount() {
        if (!hasNetworkConnection()) {
            _linkXEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        linkedAccountsUseCases.checkPendingLinkXAccountUseCase.get()().onStart {
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
                    val pendingLinkXAccountTask = result.data
                    when {
                        pendingLinkXAccountTask != null -> { // Handle pending link account task
                            linkXAccount(linkXAccountTask = pendingLinkXAccountTask)
                        }
                        else -> { // Start new link account activity
                            _linkXEventChannel.send(LinkedAccountsUiEvent.StartActivityForLinkXAccount)
                        }
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider == AccountProviders.X) {
                                    uiAccountProvider.copy(isLoading = false)
                                } else uiAccountProvider
                            }
                        )
                    }
                    _linkXEventChannel.send(UiEvent.ShowLongSnackbar(result.error.asUiText()))
                }
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun linkXAccount(
        linkXAccountTask: Task<AuthResult>
    ) {
        if (!hasNetworkConnection()) {
            _linkXEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.X) {
                            uiAccountProvider.copy(isLoading = false)
                        } else uiAccountProvider
                    }
                )
            }
            return
        }

        linkedAccountsUseCases.linkXAccountUseCase.get()(
            linkXAccountTask = linkXAccountTask
        ).onStart {
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
                                    uiAccountProvider.copy(isLinked = true)
                                } else uiAccountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        LinkXAccountError.CancellationException -> Unit
                        LinkXAccountError.Network.FirebaseAuthUnauthorizedUser -> {
                            _linkXEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> _linkXEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
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

    private fun checkPendingLinkGithubAccount() {
        if (!hasNetworkConnection()) {
            _linkGithubEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            return
        }

        linkedAccountsUseCases.checkPendingLinkGithubAccountUseCase.get()().onStart {
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
                    val pendingLinkGithubAccountTask = result.data
                    when {
                        pendingLinkGithubAccountTask != null -> { // Handle pending link account task
                            linkGithubAccount(linkGithubAccountTask = pendingLinkGithubAccountTask)
                        }
                        else -> { // Start new link account activity
                            _linkGithubEventChannel.send(
                                LinkedAccountsUiEvent.StartActivityForLinkGithubAccount
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                                if (uiAccountProvider.accountProvider == AccountProviders.GITHUB) {
                                    uiAccountProvider.copy(isLoading = false)
                                } else uiAccountProvider
                            }
                        )
                    }
                    _linkGithubEventChannel.send(UiEvent.ShowLongSnackbar(result.error.asUiText()))
                }
            }
        }.launchIn(scope = viewModelScope)
    }

    private fun linkGithubAccount(
        linkGithubAccountTask: Task<AuthResult>
    ) {
        if (!hasNetworkConnection()) {
            _linkGithubEventChannel.trySend(UiEvent.ShowOfflineSnackbar)
            _state.update {
                it.copy(
                    uiAccountProviders = it.uiAccountProviders.map { uiAccountProvider ->
                        if (uiAccountProvider.accountProvider == AccountProviders.GITHUB) {
                            uiAccountProvider.copy(isLoading = false)
                        } else uiAccountProvider
                    }
                )
            }
            return
        }

        linkedAccountsUseCases.linkGithubAccountUseCase.get()(
            linkGithubAccountTask = linkGithubAccountTask
        ).onStart {
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
                                    uiAccountProvider.copy(isLinked = true)
                                } else uiAccountProvider
                            }
                        )
                    }
                }
                is Result.Error -> {
                    when (val error = result.error) {
                        LinkGithubAccountError.CancellationException -> Unit
                        LinkGithubAccountError.Network.FirebaseAuthUnauthorizedUser -> {
                            _linkGithubEventChannel.send(UiEvent.ForceLogoutUnauthorizedUser)
                        }
                        else -> _linkGithubEventChannel.send(UiEvent.ShowLongSnackbar(error.asUiText()))
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