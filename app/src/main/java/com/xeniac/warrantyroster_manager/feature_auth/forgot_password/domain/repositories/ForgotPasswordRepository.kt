package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.models.SendResetPasswordEmailResult

interface ForgotPasswordRepository {

    suspend fun sendResetPasswordEmail(
        email: String
    ): SendResetPasswordEmailResult
}