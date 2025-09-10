package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.UnlinkAccountsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnlinkGithubAccountUseCase(
    private val unlinkAccountsRepository: UnlinkAccountsRepository
) {
    operator fun invoke(
    ): Flow<Result<Unit, UnlinkGithubAccountError>> = flow {
        return@flow emit(unlinkAccountsRepository.unlinkGithubAccount())
    }
}