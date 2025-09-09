package com.xeniac.warrantyroster_manager.feature_change_password.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError

fun ChangeUserPasswordError.asUiText(): UiText = when (this) {
    ChangeUserPasswordError.BlankCurrentPassword -> UiText.StringResource(R.string.change_password_error_blank_current_password)

    ChangeUserPasswordError.BlankNewPassword -> UiText.StringResource(R.string.change_password_error_blank_new_password)
    ChangeUserPasswordError.ShortNewPassword -> UiText.StringResource(
        R.string.change_password_error_short_new_password,
        Constants.MIN_PASSWORD_LENGTH
    )

    ChangeUserPasswordError.BlankConfirmNewPassword -> UiText.StringResource(R.string.change_password_error_blank_confirm_new_password)
    ChangeUserPasswordError.NotMatchingNewPasswords -> UiText.StringResource(R.string.change_password_error_not_matching_new_passwords)

    ChangeUserPasswordError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    ChangeUserPasswordError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    ChangeUserPasswordError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    ChangeUserPasswordError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    ChangeUserPasswordError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    ChangeUserPasswordError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    ChangeUserPasswordError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(
        R.string.change_password_invalid_credentials
    )

    ChangeUserPasswordError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}