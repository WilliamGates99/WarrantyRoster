package com.xeniac.warrantyroster_manager.core.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.errors.GetUserProfileError
import com.xeniac.warrantyroster_manager.core.domain.errors.LogoutUserError
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile

interface UserRepository {

    suspend fun getUserProfile(): Result<UserProfile, GetUserProfileError>

    suspend fun logoutUser(): Result<Unit, LogoutUserError>

    suspend fun forceLogoutUnauthorizedUser(): Result<Unit, LogoutUserError>
}