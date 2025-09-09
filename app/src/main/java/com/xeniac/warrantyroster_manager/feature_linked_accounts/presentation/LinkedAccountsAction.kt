package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders

sealed interface LinkedAccountsAction {
    data object GetLinkedAccountProviders : LinkedAccountsAction

    data class ConnectAccount(val accountProvider: AccountProviders) : LinkedAccountsAction
    data class DisconnectAccount(val accountProvider: AccountProviders) : LinkedAccountsAction
}