package com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGithubAccountRepository
import javax.inject.Inject

class LinkGithubAccountRepositoryImpl @Inject constructor(

) : LinkGithubAccountRepository {

    override suspend fun checkPendingLinkGithubAccount(): Result<Task<AuthResult>?, LinkGithubAccountError> {
        TODO("Not yet implemented")
    }

    override suspend fun linkGithubAccount(
        linkGithubAccountTask: Task<AuthResult>
    ): Result<Unit, LinkGithubAccountError> {
        TODO("Not yet implemented")
    }
}