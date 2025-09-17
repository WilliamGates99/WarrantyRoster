package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders

interface LinkedAccountsRepository {

    suspend fun getLinkedAccountProviders(): Result<Set<AccountProviders>, GetLinkedAccountProvidersError>
}