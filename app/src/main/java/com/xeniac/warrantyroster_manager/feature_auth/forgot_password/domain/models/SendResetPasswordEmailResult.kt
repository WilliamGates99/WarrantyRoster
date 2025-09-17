package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.models

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError

data class SendResetPasswordEmailResult(
    val emailError: SendResetPasswordEmailError? = null,
    val timerValueError: SendResetPasswordEmailError? = null,
    val result: Result<Unit, SendResetPasswordEmailError>? = null
)