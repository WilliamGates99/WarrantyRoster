package com.xeniac.warrantyroster_manager.core.domain.errors

sealed class LogoutUserError : Error() {
    sealed class Network : LogoutUserError() {
        data object SomethingWentWrong : Network()
    }

    sealed class Local : LogoutUserError() {
        data object SomethingWentWrong : Local()
    }
}