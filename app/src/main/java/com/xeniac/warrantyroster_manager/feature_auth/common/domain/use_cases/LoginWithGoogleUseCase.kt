package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginWithGoogleUseCase(
    private val loginWithGoogleRepository: LoginWithGoogleRepository
) {
    operator fun invoke(
        credential: Credential
    ): Flow<Result<Unit, LoginWithGoogleError>> = flow {
        return@flow emit(loginWithGoogleRepository.loginWithGoogle(credential = credential))
    }
}