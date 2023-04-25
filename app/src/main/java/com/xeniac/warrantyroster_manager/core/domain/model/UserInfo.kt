package com.xeniac.warrantyroster_manager.core.domain.model

data class UserInfo(
    val uid: String,
    val email: String?,
    val isEmailVerified: Boolean
)