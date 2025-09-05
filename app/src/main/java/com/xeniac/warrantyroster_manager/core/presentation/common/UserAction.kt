package com.xeniac.warrantyroster_manager.core.presentation.common

sealed interface UserAction {
    // data class UpdateUserProfile(val updatedUserProfile: UserProfile) : UserAction

    // data object GetUserProfile : UserAction
    // data object GetUpdatedUserProfile : UserAction

    data object Logout : UserAction
    data object ForceLogoutUnauthorizedUser : UserAction
}