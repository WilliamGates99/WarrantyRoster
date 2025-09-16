package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import dagger.Lazy

data class UpsertWarrantyUseCases(
    val observeCategoriesUseCase: Lazy<ObserveCategoriesUseCase>,
//    val addWarrantyUseCase: Lazy<AddWarrantyUseCase>,
//    val editWarrantyUseCase: Lazy<editWarrantyUseCase>,
)