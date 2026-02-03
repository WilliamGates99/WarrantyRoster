package com.xeniac.warrantyroster_manager.core.domain.use_cases

import dagger.Lazy
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
data class MainUseCases @Inject constructor(
    val getCurrentAppLocaleUseCase: Lazy<GetCurrentAppLocaleUseCase>,
    val getIsUserLoggedInUseCase: Lazy<GetIsUserLoggedInUseCase>
)