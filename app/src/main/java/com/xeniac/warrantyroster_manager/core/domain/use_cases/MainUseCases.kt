package com.xeniac.warrantyroster_manager.core.domain.use_cases

import dagger.Lazy

data class MainUseCases(
    val getCurrentAppLocaleUseCase: Lazy<GetCurrentAppLocaleUseCase>,
    val getIsUserLoggedInUseCase: Lazy<GetIsUserLoggedInUseCase>
)