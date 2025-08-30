package com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.RegisterWithGoogleError

interface LoginWithGoogleRepository {

    suspend fun registerWithGoogle(
//        account: GoogleSignInAccount
    ): Result<Unit, RegisterWithGoogleError>
}