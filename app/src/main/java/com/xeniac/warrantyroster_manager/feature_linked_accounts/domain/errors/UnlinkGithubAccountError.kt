package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class UnlinkGithubAccountError : Error() {
    sealed class Network : UnlinkGithubAccountError() {
        data object FirebaseAuthUnauthorizedUser : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data object SomethingWentWrong : Network()
    }
}