package com.xeniac.warrantyroster_manager.core.domain.model

data class TestUser(
    var email: String,
    var password: String?,
    var isEmailVerified: Boolean = false,
    val providerIds: MutableList<String> = mutableListOf(),
    val uid: String = "1"
)