package com.xeniac.warrantyroster_manager.feature_auth.register.domain.repositories

import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.SendVerificationEmailError

interface RegisterRepository {

    suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit, RegisterWithEmailError>

    suspend fun sendVerificationEmail(
        user: FirebaseUser
    ): Result<Unit, SendVerificationEmailError>
}