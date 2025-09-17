package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import dagger.Lazy

data class WarrantiesUseCases(
    val observeCategoriesUseCase: Lazy<ObserveCategoriesUseCase>,
    val observeWarrantiesUseCase: Lazy<ObserveWarrantiesUseCase>,
    val searchWarrantiesUseCase: Lazy<SearchWarrantiesUseCase>
)