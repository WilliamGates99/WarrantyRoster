package com.xeniac.warrantyroster_manager.feature_settings.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_settings.domain.errors.SendVerificationEmailError

fun SendVerificationEmailError.asUiText(): UiText = when (this) {
    SendVerificationEmailError.BlankEmail -> UiText.StringResource(R.string.settings_send_verification_email_error_email_not_provided)

    SendVerificationEmailError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    SendVerificationEmailError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    SendVerificationEmailError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    SendVerificationEmailError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    SendVerificationEmailError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    SendVerificationEmailError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    SendVerificationEmailError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}