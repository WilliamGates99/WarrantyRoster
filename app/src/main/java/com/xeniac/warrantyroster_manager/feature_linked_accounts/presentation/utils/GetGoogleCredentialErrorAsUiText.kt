package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation.utils

import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetGoogleCredentialError

fun GetGoogleCredentialError.asUiText(): UiText = when (this) {
    GetGoogleCredentialError.CancellationException -> UiText.DynamicString("User cancelled the credential selector.")

    GetGoogleCredentialError.Network.AccessCredentialManagerFailed -> UiText.StringResource(R.string.linked_accounts_google_error_access_credential_manager)
    GetGoogleCredentialError.Network.CredentialCorruptedOrExpired -> UiText.StringResource(R.string.linked_accounts_google_error_corrupted_or_expired_credential)
    GetGoogleCredentialError.Network.SomethingWentWrong -> UiText.StringResource(R.string.error_something_went_wrong)
}