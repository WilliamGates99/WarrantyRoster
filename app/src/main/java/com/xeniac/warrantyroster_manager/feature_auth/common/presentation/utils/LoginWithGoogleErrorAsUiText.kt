package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError

fun LoginWithGoogleError.asUiText(): UiText = when (this) {
    LoginWithGoogleError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LoginWithGoogleError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

//    LoginWithGoogleError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
//    LoginWithGoogleError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
//    LoginWithGoogleError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)
//
//    LoginWithGoogleError.Network.FirebaseAuthInvalidUserException -> UiText.StringResource(R.string.login_error_account_not_exist)
//    LoginWithGoogleError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.login_error_invalid_credentials)

    LoginWithGoogleError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)

    // TODO: TEMP - REMOVE
    else -> UiText.StringResource(R.string.error_something_went_wrong)
}