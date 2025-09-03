package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithFacebookError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithFacebookRepository
import timber.log.Timber
import javax.inject.Inject

class LoginWithFacebookRepositoryImpl @Inject constructor(

) : LoginWithFacebookRepository {

    override suspend fun loginWithFacebook(): Result<Unit, LoginWithFacebookError> {
        return try {
            // TODO: IMPLEMENT
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e("loginWithFacebook Exception:")
            e.printStackTrace()
            Result.Error(LoginWithFacebookError.Network.SomethingWentWrong)
        }
    }
}