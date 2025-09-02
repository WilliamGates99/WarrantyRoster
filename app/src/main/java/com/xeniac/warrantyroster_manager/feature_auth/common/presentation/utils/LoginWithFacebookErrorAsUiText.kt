package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithFacebookError

fun LoginWithFacebookError.asUiText(): UiText = when (this) {
    LoginWithFacebookError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LoginWithFacebookError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

//    LoginWithFacebookError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
//    LoginWithFacebookError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
//    LoginWithFacebookError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)
//
//    LoginWithFacebookError.Network.FirebaseAuthInvalidUserException -> UiText.StringResource(R.string.login_error_account_not_exist)
//    LoginWithFacebookError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.login_error_invalid_credentials)

    LoginWithFacebookError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)

    // TODO: TEMP - REMOVE
    else -> UiText.StringResource(R.string.error_something_went_wrong)
}