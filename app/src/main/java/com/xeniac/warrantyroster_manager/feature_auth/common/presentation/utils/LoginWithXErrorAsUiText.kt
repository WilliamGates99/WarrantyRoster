package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError

fun LoginWithXError.asUiText(): UiText = when (this) {
    LoginWithXError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LoginWithXError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

//    LoginWithXError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
//    LoginWithXError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
//    LoginWithXError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)
//
//    LoginWithXError.Network.FirebaseAuthInvalidUserException -> UiText.StringResource(R.string.login_error_account_not_exist)
//    LoginWithXError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.login_error_invalid_credentials)

    LoginWithXError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)

    // TODO: TEMP - REMOVE
    else -> UiText.StringResource(R.string.error_something_went_wrong)
}