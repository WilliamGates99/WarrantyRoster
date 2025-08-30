package com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class RegisterWithEmailError : Error() {
    data object BlankEmail : RegisterWithEmailError()
    data object InvalidEmail : RegisterWithEmailError()

    data object BlankPassword : RegisterWithEmailError()
    data object ShortPassword : RegisterWithEmailError()

    data object BlankConfirmPassword : RegisterWithEmailError()
    data object NotMatchingPasswords : RegisterWithEmailError()

    sealed class Network : RegisterWithEmailError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object SomethingWentWrong : Network()
    }
}