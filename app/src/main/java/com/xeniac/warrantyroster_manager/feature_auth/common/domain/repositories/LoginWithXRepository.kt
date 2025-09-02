package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError

interface LoginWithXRepository {

    suspend fun checkPendingLoginWithX(): Result<Task<AuthResult>?, LoginWithXError>

    suspend fun loginWithX(
        loginWithXTask: Task<AuthResult>
    ): Result<Unit, LoginWithXError>
}