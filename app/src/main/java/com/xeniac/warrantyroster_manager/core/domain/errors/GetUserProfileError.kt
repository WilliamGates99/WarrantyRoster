package com.xeniac.warrantyroster_manager.core.domain.errors

sealed class GetUserProfileError : Error() {
    sealed class Network : GetUserProfileError() {
        data object FirebaseAuthUnauthorizedUser : Network()

        data object FirebaseNetworkException : Network()
        data object FirebaseTooManyRequestsException : Network()
        data object Firebase403 : Network()

        data object SomethingWentWrong : Network()
    }
}