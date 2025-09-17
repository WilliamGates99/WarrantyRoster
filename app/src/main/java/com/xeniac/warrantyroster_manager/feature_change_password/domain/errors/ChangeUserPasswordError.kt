package com.xeniac.warrantyroster_manager.feature_change_password.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class ChangeUserPasswordError : Error() {
    data object BlankCurrentPassword : ChangeUserPasswordError()

    data object BlankNewPassword : ChangeUserPasswordError()
    data object ShortNewPassword : ChangeUserPasswordError()

    data object BlankConfirmNewPassword : ChangeUserPasswordError()
    data object NotMatchingNewPasswords : ChangeUserPasswordError()

    sealed class Network : ChangeUserPasswordError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data object FirebaseAuthUnauthorizedUser : Network()

        data object FirebaseAuthInvalidCredentialsException : Network()

        data object SomethingWentWrong : Network()
    }
}