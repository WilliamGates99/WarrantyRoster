package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError

interface LoginWithGithubRepository {

    suspend fun loginWithGithub(
//        accessToken: AccessToken
    ): Result<Unit, LoginWithGithubError>
}