package com.xeniac.warrantyroster_manager.feature_auth.register.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError

interface RegisterRepository {

    suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit, RegisterWithEmailError>
}