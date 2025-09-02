package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import javax.inject.Inject

class LoginWithGoogleRepositoryImpl @Inject constructor(

) : LoginWithGoogleRepository {

    override suspend fun loginWithGoogle(): Result<Unit, LoginWithGoogleError> {
        TODO("Not yet implemented")
    }
}