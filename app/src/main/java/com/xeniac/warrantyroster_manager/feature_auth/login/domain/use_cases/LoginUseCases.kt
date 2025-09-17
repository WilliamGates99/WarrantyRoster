package com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithGithubUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGithubUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
import dagger.Lazy

data class LoginUseCases(
    val loginWithEmailUseCase: Lazy<LoginWithEmailUseCase>,
    val getGoogleCredentialUseCase: Lazy<GetGoogleCredentialUseCase>,
    val loginWithGoogleUseCase: Lazy<LoginWithGoogleUseCase>,
    val checkPendingLoginWithXUseCase: Lazy<CheckPendingLoginWithXUseCase>,
    val loginWithXUseCase: Lazy<LoginWithXUseCase>,
    val checkPendingLoginWithGithubUseCase: Lazy<CheckPendingLoginWithGithubUseCase>,
    val loginWithGithubUseCase: Lazy<LoginWithGithubUseCase>
)