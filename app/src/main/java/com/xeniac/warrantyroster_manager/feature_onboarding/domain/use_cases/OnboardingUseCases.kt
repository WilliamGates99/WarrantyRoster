package com.xeniac.warrantyroster_manager.feature_onboarding.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import dagger.Lazy

data class OnboardingUseCases(
    val getCurrentAppLocaleUseCase: Lazy<GetCurrentAppLocaleUseCase>,
    val storeCurrentAppLocaleUseCase: Lazy<StoreCurrentAppLocaleUseCase>
)