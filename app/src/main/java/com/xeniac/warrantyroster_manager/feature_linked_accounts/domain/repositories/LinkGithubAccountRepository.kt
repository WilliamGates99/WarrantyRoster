package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGithubAccountError

interface LinkGithubAccountRepository {

    suspend fun checkPendingLinkGithubAccount(): Result<Task<AuthResult>?, LinkGithubAccountError>

    suspend fun linkGithubAccount(
        linkGithubAccountTask: Task<AuthResult>
    ): Result<Unit, LinkGithubAccountError>
}