package com.xeniac.warrantyroster_manager.core.data.mappers

import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile

fun FirebaseUser.toUserProfile(): UserProfile = UserProfile(
    uid = uid,
    email = email,
    isEmailVerified = isEmailVerified
)