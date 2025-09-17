package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGoogleAccountError

interface LinkGoogleAccountRepository {

    suspend fun getGoogleCredential(): Result<Credential, GetGoogleCredentialError>

    suspend fun linkGoogleAccount(
        credential: Credential
    ): Result<Unit, LinkGoogleAccountError>
}