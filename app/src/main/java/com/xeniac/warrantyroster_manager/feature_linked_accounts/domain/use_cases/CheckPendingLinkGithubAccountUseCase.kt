package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGithubAccountRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class CheckPendingLinkGithubAccountUseCase @Inject constructor(
    private val linkGithubAccountRepository: LinkGithubAccountRepository
) {
    operator fun invoke(): Flow<Result<Task<AuthResult>?, LinkGithubAccountError>> = flow {
        return@flow emit(linkGithubAccountRepository.checkPendingLinkGithubAccount())
    }
}