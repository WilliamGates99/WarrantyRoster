package com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import dagger.Lazy

data class SettingsUseCases(
    val getCurrentAppLocaleUseCase: Lazy<GetCurrentAppLocaleUseCase>,
    val getCurrentAppThemeUseCase: Lazy<GetCurrentAppThemeUseCase>,
    val storeCurrentAppLocaleUseCase: Lazy<StoreCurrentAppLocaleUseCase>,
    val storeCurrentAppThemeUseCase: Lazy<StoreCurrentAppThemeUseCase>
)