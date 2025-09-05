package com.xeniac.warrantyroster_manager.core.domain.use_cases

import dagger.Lazy

data class UserUseCases(
    val logoutUserUseCase: Lazy<LogoutUserUseCase>,
    val forceLogoutUnauthorizedUserUseCase: Lazy<ForceLogoutUnauthorizedUserUseCase>
)