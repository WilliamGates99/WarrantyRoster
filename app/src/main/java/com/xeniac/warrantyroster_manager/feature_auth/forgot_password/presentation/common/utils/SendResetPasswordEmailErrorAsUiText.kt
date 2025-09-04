package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError

fun SendResetPasswordEmailError.asUiText(): UiText = when (this) {
    SendResetPasswordEmailError.BlankEmail -> UiText.StringResource(R.string.forgot_pw_error_blank_email)
    SendResetPasswordEmailError.InvalidEmail -> UiText.StringResource(R.string.forgot_pw_error_invalid_email)

    is SendResetPasswordEmailError.TimerIsTicking -> UiText.PluralStringResource(
        R.plurals.forgot_pw_error_timer_is_not_zero,
        this.timerValueInSeconds,
        this.timerValueInSeconds
    )

    SendResetPasswordEmailError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    SendResetPasswordEmailError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    SendResetPasswordEmailError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    SendResetPasswordEmailError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    SendResetPasswordEmailError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    SendResetPasswordEmailError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}