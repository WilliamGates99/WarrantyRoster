package com.xeniac.warrantyroster_manager.core.data.mapper

import com.xeniac.warrantyroster_manager.core.data.dto.TestUserDto
import com.xeniac.warrantyroster_manager.core.domain.model.TestUser

fun TestUserDto.toTestUser(): TestUser = TestUser(
    email = email,
    password = password,
    isEmailVerified = isEmailVerified,
    providerIds = providerIds,
    uid = uid
)

fun TestUser.toTestUserDto(): TestUserDto = TestUserDto(
    email = email,
    password = password,
    isEmailVerified = isEmailVerified,
    providerIds = providerIds,
    uid = uid
)