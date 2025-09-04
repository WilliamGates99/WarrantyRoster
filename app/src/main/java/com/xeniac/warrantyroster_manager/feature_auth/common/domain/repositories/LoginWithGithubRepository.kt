package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError

interface LoginWithGithubRepository {

    suspend fun checkPendingLoginWithGithub(): Result<Task<AuthResult>?, LoginWithGithubError>

    suspend fun loginWithGithub(
        loginWithGithubTask: Task<AuthResult>
    ): Result<Unit, LoginWithGithubError>
}