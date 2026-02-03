package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetLinkedAccountProvidersError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkedAccountsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class GetLinkedAccountProvidersUseCase @Inject constructor(
    private val linkedAccountsRepository: LinkedAccountsRepository
) {
    operator fun invoke(
    ): Flow<Result<Set<AccountProviders>, GetLinkedAccountProvidersError>> = flow {
        return@flow emit(linkedAccountsRepository.getLinkedAccountProviders())
    }
}