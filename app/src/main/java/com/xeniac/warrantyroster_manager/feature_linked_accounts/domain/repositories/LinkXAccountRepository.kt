package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkXAccountError

interface LinkXAccountRepository {

    suspend fun checkPendingLinkXAccount(): Result<Task<AuthResult>?, LinkXAccountError>

    suspend fun linkXAccount(
        linkXAccountTask: Task<AuthResult>
    ): Result<Unit, LinkXAccountError>
}