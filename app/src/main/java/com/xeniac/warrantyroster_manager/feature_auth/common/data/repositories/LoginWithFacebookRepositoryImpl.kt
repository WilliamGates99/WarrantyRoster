package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithFacebookError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithFacebookRepository
import javax.inject.Inject

class LoginWithFacebookRepositoryImpl @Inject constructor(

) : LoginWithFacebookRepository {

    override suspend fun loginWithFacebook(): Result<Unit, LoginWithFacebookError> {
        TODO("Not yet implemented")
    }
}