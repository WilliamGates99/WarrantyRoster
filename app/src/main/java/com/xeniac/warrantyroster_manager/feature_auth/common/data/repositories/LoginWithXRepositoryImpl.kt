package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import javax.inject.Inject

class LoginWithXRepositoryImpl @Inject constructor(

) : LoginWithXRepository {

    override suspend fun loginWithX(): Result<Unit, LoginWithXError> {
        TODO("Not yet implemented")
    }
}