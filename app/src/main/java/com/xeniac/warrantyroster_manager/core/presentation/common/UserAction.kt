package com.xeniac.warrantyroster_manager.core.presentation.common

sealed interface UserAction {
    data object GetUserProfile : UserAction

    data object Logout : UserAction
    data object ForceLogoutUnauthorizedUser : UserAction
}