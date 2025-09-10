package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class GetGoogleCredentialError : Error() {
    data object CancellationException : GetGoogleCredentialError()

    sealed class Network : GetGoogleCredentialError() {
        data object AccessCredentialManagerFailed : Network()
        data object CredentialCorruptedOrExpired : Network()
        data object SomethingWentWrong : Network()
    }
}