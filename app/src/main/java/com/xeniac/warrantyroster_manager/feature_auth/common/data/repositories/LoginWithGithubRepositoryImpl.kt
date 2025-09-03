package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGithubRepository
import timber.log.Timber
import javax.inject.Inject

class LoginWithGithubRepositoryImpl @Inject constructor(

) : LoginWithGithubRepository {

    override suspend fun loginWithGithub(): Result<Unit, LoginWithGithubError> {
        return try {
            // TODO: IMPLEMENT
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e("loginWithGithub Exception:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.SomethingWentWrong)
        }
    }
}