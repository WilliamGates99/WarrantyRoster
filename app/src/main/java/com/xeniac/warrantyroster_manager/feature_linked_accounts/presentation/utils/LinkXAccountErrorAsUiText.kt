package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkXAccountError

fun LinkXAccountError.asUiText(): UiText = when (this) {
    LinkXAccountError.CancellationException -> UiText.DynamicString("The web operation was canceled by the user.")

    LinkXAccountError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LinkXAccountError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    LinkXAccountError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    LinkXAccountError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    LinkXAccountError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    LinkXAccountError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    LinkXAccountError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(R.string.linked_accounts_error_corrupted_or_expired_credential)
    LinkXAccountError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.linked_accounts_error_email_exists_with_different_credentials)

    LinkXAccountError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}