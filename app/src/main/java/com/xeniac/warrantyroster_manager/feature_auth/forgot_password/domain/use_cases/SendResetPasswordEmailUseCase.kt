package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.models.SendResetPasswordEmailResult
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.ForgotPasswordRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.validation.ValidateEmail
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.validation.ValidateTimerValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SendResetPasswordEmailUseCase(
    private val forgotPasswordRepository: ForgotPasswordRepository,
    private val validateEmail: ValidateEmail,
    private val validateTimerValue: ValidateTimerValue
) {
    operator fun invoke(
        email: String,
        timerValueInSeconds: Int
    ): Flow<SendResetPasswordEmailResult> = flow {
        val emailError = validateEmail(email)
        val timerValueError = validateTimerValue(timerValueInSeconds)

        val hasError = listOf(
            emailError,
            timerValueError
        ).any { it != null }

        if (hasError) {
            return@flow emit(
                SendResetPasswordEmailResult(
                    emailError = timerValueError,
                    timerValueError = timerValueError
                )
            )
        }

        return@flow emit(
            forgotPasswordRepository.sendResetPasswordEmail(
                email = email
            )
        )
    }
}