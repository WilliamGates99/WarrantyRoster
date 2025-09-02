package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError

interface LoginWithGoogleRepository {

    suspend fun getGoogleCredential(): Result<Credential, GetGoogleCredentialError>

    suspend fun loginWithGoogle(
        credential: Credential
    ): Result<Unit, LoginWithGoogleError>
}