package com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_settings.domain.errors.SendVerificationEmailError
import com.xeniac.warrantyroster_manager.feature_settings.domain.repositories.AccountVerificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SendVerificationEmailUseCase(
    private val accountVerificationRepository: AccountVerificationRepository
) {
    operator fun invoke(): Flow<Result<Unit, SendVerificationEmailError>> = flow {
        return@flow emit(accountVerificationRepository.sendVerificationEmail())
    }
}