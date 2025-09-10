package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

sealed interface LinkedAccountsAction {
    data object GetLinkedAccountProviders : LinkedAccountsAction

    data object LinkGoogleAccount : LinkedAccountsAction
    data object LinkXAccount : LinkedAccountsAction
    data object LinkGithubAccount : LinkedAccountsAction

    data object UnlinkGoogleAccount : LinkedAccountsAction
    data object UnlinkXAccount : LinkedAccountsAction
    data object UnlinkGithubAccount : LinkedAccountsAction
}