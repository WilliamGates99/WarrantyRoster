package com.xeniac.warrantyroster_manager.core.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.core.domain.model.UserInfo

fun FirebaseUser.toUserInfo(): UserInfo = UserInfo(
    uid = uid,
    email = email,
    isEmailVerified = isEmailVerified
)