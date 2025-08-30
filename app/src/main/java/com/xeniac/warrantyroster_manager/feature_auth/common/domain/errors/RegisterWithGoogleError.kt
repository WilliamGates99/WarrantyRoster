package com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

sealed class RegisterWithGoogleError : Error() {
    sealed class Network : RegisterWithGoogleError() {
        data object Offline : Network()
        data object ConnectTimeoutException : Network()
        data object HttpRequestTimeoutException : Network()
        data object SocketTimeoutException : Network()
        data object SerializationException : Network()
        data object JsonConvertException : Network()
        data object SSLHandshakeException : Network()
        data object CertPathValidatorException : Network()

        // 3xx errors
        data object RedirectResponseException : Network()

        // 4xx errors
        data object TooManyRequests : Network()
        data object ClientRequestException : Network()

        // 5xx
        data object ServerResponseException : Network()

        data object SomethingWentWrong : Network()
    }
}