package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGoogleAccountRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class GetGoogleCredentialUseCase @Inject constructor(
    private val linkGoogleAccountRepository: LinkGoogleAccountRepository
) {
    operator fun invoke(): Flow<Result<Credential, GetGoogleCredentialError>> = flow {
        return@flow emit(linkGoogleAccountRepository.getGoogleCredential())
    }
}