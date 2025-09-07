package com.xeniac.warrantyroster_manager.core.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.errors.GetUserProfileError
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.models.UserProfile
import com.xeniac.warrantyroster_manager.core.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<UserProfile, GetUserProfileError>> = flow {
        return@flow emit(userRepository.getUserProfile())
    }
}