package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGithubRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class CheckPendingLoginWithGithubUseCase @Inject constructor(
    private val loginWithGithubRepository: LoginWithGithubRepository
) {
    operator fun invoke(): Flow<Result<Task<AuthResult>?, LoginWithGithubError>> = flow {
        return@flow emit(loginWithGithubRepository.checkPendingLoginWithGithub())
    }
}