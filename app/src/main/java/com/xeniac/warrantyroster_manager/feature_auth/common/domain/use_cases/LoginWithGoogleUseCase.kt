package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class LoginWithGoogleUseCase @Inject constructor(
    private val loginWithGoogleRepository: LoginWithGoogleRepository
) {
    operator fun invoke(
        credential: Credential
    ): Flow<Result<Unit, LoginWithGoogleError>> = flow {
        return@flow emit(loginWithGoogleRepository.loginWithGoogle(credential = credential))
    }
}