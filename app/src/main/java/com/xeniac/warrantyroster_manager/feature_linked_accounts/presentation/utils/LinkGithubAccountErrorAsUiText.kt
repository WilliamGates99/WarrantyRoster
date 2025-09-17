package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGithubAccountError

fun LinkGithubAccountError.asUiText(): UiText = when (this) {
    LinkGithubAccountError.CancellationException -> UiText.DynamicString("The web operation was canceled by the user.")
    LinkGithubAccountError.AnotherOperationIsInProgress -> UiText.StringResource(R.string.linked_accounts_error_another_operation_is_in_progress)

    LinkGithubAccountError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LinkGithubAccountError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    LinkGithubAccountError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    LinkGithubAccountError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    LinkGithubAccountError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    LinkGithubAccountError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    LinkGithubAccountError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(
        R.string.linked_accounts_error_corrupted_or_expired_credential
    )
    LinkGithubAccountError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.linked_accounts_error_email_exists_with_different_credentials)

    LinkGithubAccountError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}