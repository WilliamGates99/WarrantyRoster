package com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithFacebookUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
import dagger.Lazy

data class RegisterUseCases(
    val registerWithEmailUseCase: Lazy<RegisterWithEmailUseCase>,
    val getGoogleCredentialUseCase: Lazy<GetGoogleCredentialUseCase>,
    val loginWithGoogleUseCase: Lazy<LoginWithGoogleUseCase>,
    val checkPendingLoginWithXUseCase: Lazy<CheckPendingLoginWithXUseCase>,
    val loginWithXUseCase: Lazy<LoginWithXUseCase>,
    val loginWithFacebookUseCase: Lazy<LoginWithFacebookUseCase>
)