package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    fun observeCategories(): Flow<Result<List<WarrantyCategory>, ObserveCategoriesError>>

    // suspend fun getCategoryById(): Result<WarrantyCategory, GetCategoryByIdError>
}