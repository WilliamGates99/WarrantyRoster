package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.RegisterWithFacebookError

interface LoginWithFacebookRepository {

    suspend fun registerWithFacebook(
//        accessToken: AccessToken
    ): Result<Unit, RegisterWithFacebookError>
}