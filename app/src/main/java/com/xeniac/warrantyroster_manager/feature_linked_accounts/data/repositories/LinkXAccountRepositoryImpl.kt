package com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkXAccountRepository
import javax.inject.Inject

class LinkXAccountRepositoryImpl @Inject constructor(

) : LinkXAccountRepository {

    override suspend fun checkPendingLinkXAccount(): Result<Task<AuthResult>?, LinkXAccountError> {
        TODO("Not yet implemented")
    }

    override suspend fun linkXAccount(
        linkXAccountTask: Task<AuthResult>
    ): Result<Unit, LinkXAccountError> {
        TODO("Not yet implemented")
    }
}