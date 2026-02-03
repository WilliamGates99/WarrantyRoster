package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.use_cases

import androidx.credentials.Credential
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGoogleAccountRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class LinkGoogleAccountUseCase @Inject constructor(
    private val linkGoogleAccountRepository: LinkGoogleAccountRepository
) {
    operator fun invoke(
        credential: Credential
    ): Flow<Result<Unit, LinkGoogleAccountError>> = flow {
        return@flow emit(linkGoogleAccountRepository.linkGoogleAccount(credential = credential))
    }
}