package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class GetGoogleCredentialUseCase @Inject constructor(
    private val loginWithGoogleRepository: LoginWithGoogleRepository
) {
    operator fun invoke(
        shouldUseFallbackAccountPicker: Boolean = false
    ): Flow<Result<Credential, GetGoogleCredentialError>> = flow {
        return@flow emit(
            loginWithGoogleRepository.getGoogleCredential(
                shouldUseFallbackAccountPicker = shouldUseFallbackAccountPicker
            )
        )
    }
}