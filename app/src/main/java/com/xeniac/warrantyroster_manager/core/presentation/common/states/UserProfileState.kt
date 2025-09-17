package com.xeniac.warrantyroster_manager.core.presentation.common.states

import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile

data class UserProfileState(
    val userProfile: UserProfile? = null,
    val isUserProfileLoading: Boolean = true,
    val isLogoutLoading: Boolean = false
)