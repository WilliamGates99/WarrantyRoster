package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event

sealed class LinkedAccountsUiEvent : Event() {
    data object StartActivityForLinkXAccount : LinkedAccountsUiEvent()
    data object StartActivityForLinkGithubAccount : LinkedAccountsUiEvent()
}