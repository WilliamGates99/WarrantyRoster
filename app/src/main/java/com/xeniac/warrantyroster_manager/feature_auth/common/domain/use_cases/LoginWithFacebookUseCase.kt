package com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithFacebookError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithFacebookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginWithFacebookUseCase(
    private val loginWithFacebookRepository: LoginWithFacebookRepository
) {
    operator fun invoke(): Flow<Result<Unit, LoginWithFacebookError>> = flow {
        return@flow emit(loginWithFacebookRepository.loginWithFacebook())
    }
}