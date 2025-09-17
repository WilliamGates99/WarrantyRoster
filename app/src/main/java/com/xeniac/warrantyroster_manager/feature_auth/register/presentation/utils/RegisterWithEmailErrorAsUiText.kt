package com.xeniac.warrantyroster_manager.feature_auth.register.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError

fun RegisterWithEmailError.asUiText(): UiText = when (this) {
    RegisterWithEmailError.BlankEmail -> UiText.StringResource(R.string.register_error_blank_email)
    RegisterWithEmailError.InvalidEmail -> UiText.StringResource(R.string.register_error_invalid_email)

    RegisterWithEmailError.BlankPassword -> UiText.StringResource(R.string.register_error_blank_password)
    RegisterWithEmailError.ShortPassword -> UiText.StringResource(
        R.string.register_error_short_password,
        Constants.MIN_PASSWORD_LENGTH
    )

    RegisterWithEmailError.BlankConfirmPassword -> UiText.StringResource(R.string.register_error_blank_confirm_password)
    RegisterWithEmailError.NotMatchingPasswords -> UiText.StringResource(R.string.register_error_not_matching_passwords)

    RegisterWithEmailError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    RegisterWithEmailError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    RegisterWithEmailError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    RegisterWithEmailError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    RegisterWithEmailError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    RegisterWithEmailError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.register_error_account_with_same_email_exists)

    RegisterWithEmailError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}