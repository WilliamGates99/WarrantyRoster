package com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class SendVerificationEmailError : Error() {
    sealed class Network : SendVerificationEmailError() {
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        data object SomethingWentWrong : Network()
    }
}