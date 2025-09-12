package com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class LinkGithubAccountError : Error() {
    data object CancellationException : LinkGithubAccountError()
    data object AnotherOperationIsInProgress : LinkGithubAccountError()

    sealed class Network : LinkGithubAccountError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data object FirebaseAuthUnauthorizedUser : Network()

        data object FirebaseAuthInvalidCredentialsException : Network()
        data object FirebaseAuthUserCollisionException : Network()

        data object SomethingWentWrong : Network()
    }
}