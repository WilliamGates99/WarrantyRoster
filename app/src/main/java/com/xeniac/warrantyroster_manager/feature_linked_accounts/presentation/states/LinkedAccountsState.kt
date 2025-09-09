package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.states

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders

data class LinkedAccountsState(
    val accountProviders: List<AccountProviderUi> = AccountProviders.entries.map { accountProvider ->
        AccountProviderUi(accountProvider = accountProvider)
    },
    val errorMessage: UiText? = null,
    val isLinkedAccountProvidersLoading: Boolean = false
)