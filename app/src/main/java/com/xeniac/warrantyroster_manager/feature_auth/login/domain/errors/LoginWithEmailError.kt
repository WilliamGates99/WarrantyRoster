package com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class LoginWithEmailError : Error() {
    data object BlankEmail : LoginWithEmailError()
    data object InvalidEmail : LoginWithEmailError()

    data object BlankPassword : LoginWithEmailError()

    sealed class Network : LoginWithEmailError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object SomethingWentWrong : Network()
    }
}