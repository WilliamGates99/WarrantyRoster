package com.xeniac.warrantyroster_manager.core.domain.use_cases

import dagger.Lazy
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
data class UserUseCases @Inject constructor(
    val getUserProfileUseCase: Lazy<GetUserProfileUseCase>,
    val logoutUserUseCase: Lazy<LogoutUserUseCase>,
    val forceLogoutUnauthorizedUserUseCase: Lazy<ForceLogoutUnauthorizedUserUseCase>
)