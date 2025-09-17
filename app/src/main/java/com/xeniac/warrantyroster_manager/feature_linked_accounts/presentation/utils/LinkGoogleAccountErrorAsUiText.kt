package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGoogleAccountError

fun LinkGoogleAccountError.asUiText(): UiText = when (this) {
    LinkGoogleAccountError.Network.SSLHandshakeException -> UiText.StringResource(R.string.error_network_ssl_handshake)
    LinkGoogleAccountError.Network.CertPathValidatorException -> UiText.StringResource(R.string.error_network_cert_path_validator)

    LinkGoogleAccountError.Network.UnexpectedCredentialType -> UiText.StringResource(R.string.linked_accounts_google_error_unexpected_credential_type)
    LinkGoogleAccountError.Network.GoogleIdTokenParsingException -> UiText.StringResource(R.string.linked_accounts_google_error_invalid_google_id_token)

    LinkGoogleAccountError.Network.FirebaseNetworkException -> UiText.StringResource(R.string.error_network_failure)
    LinkGoogleAccountError.Network.FirebaseTooManyRequestsException -> UiText.StringResource(R.string.error_firebase_device_blocked)
    LinkGoogleAccountError.Network.Firebase403 -> UiText.StringResource(R.string.error_firebase_403)

    LinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser -> UiText.StringResource(R.string.error_firebase_unauthorized_user)

    LinkGoogleAccountError.Network.FirebaseAuthInvalidCredentialsException -> UiText.StringResource(
        R.string.linked_accounts_error_corrupted_or_expired_credential
    )
    LinkGoogleAccountError.Network.FirebaseAuthUserCollisionException -> UiText.StringResource(R.string.linked_accounts_error_email_exists_with_different_credentials)

    LinkGoogleAccountError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}