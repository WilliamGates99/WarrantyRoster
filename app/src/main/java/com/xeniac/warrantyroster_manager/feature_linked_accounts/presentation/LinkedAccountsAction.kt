package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

sealed interface LinkedAccountsAction {
    data object GetLinkedAccountProviders : LinkedAccountsAction
}