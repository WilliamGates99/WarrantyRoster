package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.states

import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders

data class AccountProviderUi(
    val accountProvider: AccountProviders,
    val isConnected: Boolean = false,
    val isLoading: Boolean = false
)