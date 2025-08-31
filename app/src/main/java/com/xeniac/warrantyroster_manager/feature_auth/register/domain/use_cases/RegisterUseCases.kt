package com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases

import dagger.Lazy

data class RegisterUseCases(
    val registerWithEmailUseCase: Lazy<RegisterWithEmailUseCase>
)