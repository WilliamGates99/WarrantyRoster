package com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class LoginWithGoogleError : Error() {
    sealed class Network : LoginWithGoogleError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object UnexpectedCredentialType : Network()
        data object GoogleIdTokenParsingException : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data object FirebaseAuthInvalidUserException : Network()
        data object FirebaseAuthInvalidCredentialsException : Network()
        data object FirebaseAuthUserCollisionException : Network()

        data object SomethingWentWrong : Network()
    }
}