package com.xeniac.warrantyroster_manager.feature_auth.login.domain.repositories

import com.xeniac.warrantyroster_manager.feature_auth.login.domain.models.LoginWithEmailResult

interface LoginWithEmailRepository {

    suspend fun loginWithEmail(
        email: String,
        password: String
    ): LoginWithEmailResult
}