package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError

interface LoginWithGoogleRepository {

    suspend fun loginWithGoogle(
//        account: GoogleSignInAccount
    ): Result<Unit, LoginWithGoogleError>
}