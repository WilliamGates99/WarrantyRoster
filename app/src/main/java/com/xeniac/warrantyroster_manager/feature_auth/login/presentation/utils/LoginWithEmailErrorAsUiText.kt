package com.xeniac.warrantyroster_manager.feature_auth.login.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError

fun LoginWithEmailError.asUiText(): UiText = when (this) {
    LoginWithEmailError.BlankEmail -> UiText.StringResource(R.string.login_error_blank_email)
    LoginWithEmailError.InvalidEmail -> UiText.StringResource(R.string.login_error_invalid_email)

    LoginWithEmailError.BlankPassword -> UiText.StringResource(R.string.login_error_blank_password)

    LoginWithEmailError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LoginWithEmailError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    LoginWithEmailError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    LoginWithEmailError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    LoginWithEmailError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    LoginWithEmailError.Network.FirebaseAuthInvalidUserException -> UiText.StringResource(R.string.login_error_account_not_exist)
    LoginWithEmailError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.login_error_invalid_credentials)
    LoginWithEmailError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.login_error_email_exists_with_different_credentials)

    LoginWithEmailError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}