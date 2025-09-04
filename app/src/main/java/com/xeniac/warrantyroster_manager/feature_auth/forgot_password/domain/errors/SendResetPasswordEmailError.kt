package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class SendResetPasswordEmailError : Error() {
    data object BlankEmail : SendResetPasswordEmailError()
    data object InvalidEmail : SendResetPasswordEmailError()

    data class TimerIsTicking(val timerValueInSeconds: Int) : SendResetPasswordEmailError()

    sealed class Network : SendResetPasswordEmailError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data object FirebaseAuthInvalidUserException : Network()
        data object FirebaseAuthInvalidCredentialsException : Network()
        data object FirebaseAuthUserCollisionException : Network()

        data object SomethingWentWrong : Network()
    }
}