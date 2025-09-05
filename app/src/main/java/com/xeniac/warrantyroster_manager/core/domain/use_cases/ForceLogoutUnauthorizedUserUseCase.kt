package com.xeniac.warrantyroster_manager.core.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.errors.LogoutUserError
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ForceLogoutUnauthorizedUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<Unit, LogoutUserError>> = flow {
        return@flow emit(userRepository.forceLogoutUnauthorizedUser())
    }
}