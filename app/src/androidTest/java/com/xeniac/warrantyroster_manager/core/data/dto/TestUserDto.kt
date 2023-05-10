package com.xeniac.warrantyroster_manager.core.data.dto

data class TestUserDto(
    var email: String,
    var password: String?,
    var isEmailVerified: Boolean = false,
    val providerIds: MutableList<String> = mutableListOf(),
    val uid: String = "1"
)