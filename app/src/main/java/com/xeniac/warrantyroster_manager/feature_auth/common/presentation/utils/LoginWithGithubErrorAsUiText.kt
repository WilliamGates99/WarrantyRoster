package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError

fun LoginWithGithubError.asUiText(): UiText = when (this) {
    LoginWithGithubError.CancellationException -> UiText.DynamicString("The web operation was canceled by the user.")
    LoginWithGithubError.AnotherOperationIsInProgress -> UiText.StringResource(R.string.auth_other_methods_error_another_operation_is_in_progress)

    LoginWithGithubError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LoginWithGithubError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    LoginWithGithubError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    LoginWithGithubError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    LoginWithGithubError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    LoginWithGithubError.Network.FirebaseAuthInvalidUserException -> UiText.StringResource(R.string.auth_other_methods_error_account_not_exist)
    LoginWithGithubError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.auth_other_methods_error_corrupted_or_expired_credential)
    LoginWithGithubError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.auth_other_methods_error_email_exists_with_different_credentials)

    LoginWithGithubError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}