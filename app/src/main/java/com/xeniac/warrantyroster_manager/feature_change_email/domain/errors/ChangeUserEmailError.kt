package com.xeniac.warrantyroster_manager.feature_change_email.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class ChangeUserEmailError : Error() {
    data object BlankPassword : ChangeUserEmailError()

    data object BlankNewEmail : ChangeUserEmailError()
    data object InvalidNewEmail : ChangeUserEmailError()
    data object SameNewEmailAsCurrentEmail : ChangeUserEmailError()

    sealed class Network : ChangeUserEmailError() {
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