package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginWithXUseCase(
    private val loginWithXRepository: LoginWithXRepository
) {
    operator fun invoke(): Flow<Result<Unit, LoginWithXError>> = flow {
        return@flow emit(loginWithXRepository.loginWithX())
    }
}