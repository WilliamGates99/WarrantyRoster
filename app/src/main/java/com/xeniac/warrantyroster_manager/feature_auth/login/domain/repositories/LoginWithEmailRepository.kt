package com.xeniac.warrantyroster_manager.feature_auth.login.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError

interface LoginWithEmailRepository {

    suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit, LoginWithEmailError>
}