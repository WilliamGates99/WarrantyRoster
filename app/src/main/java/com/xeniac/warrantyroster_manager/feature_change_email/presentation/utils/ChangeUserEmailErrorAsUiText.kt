package com.xeniac.warrantyroster_manager.feature_change_email.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError

fun ChangeUserEmailError.asUiText(): UiText = when (this) {
    ChangeUserEmailError.BlankPassword -> UiText.StringResource(R.string.change_email_error_blank_password)

    ChangeUserEmailError.BlankNewEmail -> UiText.StringResource(R.string.change_email_error_blank_new_email)
    ChangeUserEmailError.InvalidNewEmail -> UiText.StringResource(R.string.change_email_error_invalid_new_email)
    ChangeUserEmailError.SameNewEmailAsCurrentEmail -> UiText.StringResource(R.string.change_email_error_same_email_as_current)

    ChangeUserEmailError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    ChangeUserEmailError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    ChangeUserEmailError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    ChangeUserEmailError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    ChangeUserEmailError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    ChangeUserEmailError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    ChangeUserEmailError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.change_email_invalid_credentials)
    ChangeUserEmailError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.change_email_error_account_with_same_email_exists)

    ChangeUserEmailError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}