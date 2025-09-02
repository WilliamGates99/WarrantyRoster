package com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithFacebookUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
import dagger.Lazy

data class LoginUseCases(
    val loginWithEmailUseCase: Lazy<LoginWithEmailUseCase>,
    val loginWithGoogleUseCase: Lazy<LoginWithGoogleUseCase>,
    val loginWithXUseCase: Lazy<LoginWithXUseCase>,
    val loginWithFacebookUseCase: Lazy<LoginWithFacebookUseCase>
)