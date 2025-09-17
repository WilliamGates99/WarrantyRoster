package com.xeniac.warrantyroster_manager.feature_settings.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_settings.domain.errors.SendVerificationEmailError

interface AccountVerificationRepository {

    suspend fun sendVerificationEmail(): Result<Unit, SendVerificationEmailError>
}