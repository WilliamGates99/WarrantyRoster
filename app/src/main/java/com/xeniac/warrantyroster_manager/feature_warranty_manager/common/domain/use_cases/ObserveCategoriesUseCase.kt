package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.repositories.CategoriesRepository
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke(): Flow<Result<List<WarrantyCategory>, ObserveCategoriesError>> =
        categoriesRepository.observeCategories()
}