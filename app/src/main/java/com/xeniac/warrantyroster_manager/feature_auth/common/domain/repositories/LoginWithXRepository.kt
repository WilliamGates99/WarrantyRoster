package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError

interface LoginWithXRepository {

    suspend fun loginWithX(
//        credential: AuthCredential
    ): Result<Unit, LoginWithXError>
}