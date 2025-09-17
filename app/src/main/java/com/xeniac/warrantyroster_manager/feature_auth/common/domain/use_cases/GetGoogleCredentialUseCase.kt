package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetGoogleCredentialUseCase(
    private val loginWithGoogleRepository: LoginWithGoogleRepository
) {
    operator fun invoke(): Flow<Result<Credential, GetGoogleCredentialError>> = flow {
        return@flow emit(loginWithGoogleRepository.getGoogleCredential())
    }
}