package com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class GetGoogleCredentialError : Error() {
    sealed class Network : GetGoogleCredentialError() {
        data object GetGoogleCredentialCancellationException : Network()
        data object AccessCredentialManagerFailed : Network()
        data object CredentialCorruptedOrExpired : Network()
        data object SomethingWentWrong : Network()
    }
}