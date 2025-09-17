package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginWithGithubUseCase(
    private val loginWithGithubRepository: LoginWithGithubRepository
) {
    operator fun invoke(
        loginWithGithubTask: Task<AuthResult>
    ): Flow<Result<Unit, LoginWithGithubError>> = flow {
        return@flow emit(
            loginWithGithubRepository.loginWithGithub(
                loginWithGithubTask = loginWithGithubTask
            )
        )
    }
}