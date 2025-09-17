package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkXAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CheckPendingLinkXAccountUseCase(
    private val linkXAccountRepository: LinkXAccountRepository
) {
    operator fun invoke(): Flow<Result<Task<AuthResult>?, LinkXAccountError>> = flow {
        return@flow emit(linkXAccountRepository.checkPendingLinkXAccount())
    }
}