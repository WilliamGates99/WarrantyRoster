package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import dagger.Lazy
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
data class WarrantiesUseCases @Inject constructor(
    val observeCategoriesUseCase: Lazy<ObserveCategoriesUseCase>,
    val observeWarrantiesUseCase: Lazy<ObserveWarrantiesUseCase>,
    val searchWarrantiesUseCase: Lazy<SearchWarrantiesUseCase>
)