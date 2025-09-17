package com.xeniac.warrantyroster_manager.core.domain.models

data class UserProfile(
    val uid: String,
    val email: String?,
    val isEmailVerified: Boolean
)