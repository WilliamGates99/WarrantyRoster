package com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases

import dagger.Lazy

data class LoginUseCases(
    val loginWithEmailUseCase: Lazy<LoginWithEmailUseCase>
)