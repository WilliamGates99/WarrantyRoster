package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import dagger.Lazy
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
data class UpsertWarrantyUseCases @Inject constructor(
    val observeCategoriesUseCase: Lazy<ObserveCategoriesUseCase>,
    val addWarrantyUseCase: Lazy<AddWarrantyUseCase>,
    val editWarrantyUseCase: Lazy<EditWarrantyUseCase>
)