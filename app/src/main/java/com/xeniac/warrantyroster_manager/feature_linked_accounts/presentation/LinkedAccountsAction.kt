package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

sealed interface LinkedAccountsAction {
    data object GetLinkedAccountProviders : LinkedAccountsAction

    data object ConnectGoogleAccount : LinkedAccountsAction
    data object ConnectXAccount : LinkedAccountsAction
    data object ConnectGithubAccount : LinkedAccountsAction

    data object DisconnectGoogleAccount : LinkedAccountsAction
    data object DisconnectXAccount : LinkedAccountsAction
    data object DisconnectGithubAccount : LinkedAccountsAction
}