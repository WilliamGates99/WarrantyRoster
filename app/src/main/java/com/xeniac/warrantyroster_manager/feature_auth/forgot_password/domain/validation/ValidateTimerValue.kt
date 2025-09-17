package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.validation

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError

class ValidateTimerValue {
    operator fun invoke(
        timerValueInSeconds: Int
    ): SendResetPasswordEmailError? {
        val isTimerTicking = timerValueInSeconds > 0
        if (isTimerTicking) {
            return SendResetPasswordEmailError.TimerIsTicking(timerValueInSeconds)
        }

        return null
    }
}