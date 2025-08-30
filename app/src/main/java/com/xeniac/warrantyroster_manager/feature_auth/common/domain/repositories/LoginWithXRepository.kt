package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.RegisterWithXError

interface LoginWithXRepository {

    suspend fun registerWithX(
//        credential: AuthCredential
    ): Result<Unit, RegisterWithXError>
}