package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

sealed interface LinkedAccountsAction {
    data object GetLinkedAccountProviders : LinkedAccountsAction

    data object LinkGoogleAccount : LinkedAccountsAction

    data object CheckPendingLinkXAccount : LinkedAccountsAction
    data class LinkXAccount(val linkXAccountTask: Task<AuthResult>) : LinkedAccountsAction

    data object CheckPendingLinkGithubAccount : LinkedAccountsAction
    data class LinkGithubAccount(val linkGithubAccountTask: Task<AuthResult>) : LinkedAccountsAction

    data object UnlinkGoogleAccount : LinkedAccountsAction
    data object UnlinkXAccount : LinkedAccountsAction
    data object UnlinkGithubAccount : LinkedAccountsAction
}