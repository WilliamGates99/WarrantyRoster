package com.xeniac.warrantyroster_manager.core.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.errors.LogoutUserError
import com.xeniac.warrantyroster_manager.core.domain.models.Result

interface UserRepository {

    suspend fun logoutUser(): Result<Unit, LogoutUserError>

    suspend fun forceLogoutUnauthorizedUser(): Result<Unit, LogoutUserError>
}