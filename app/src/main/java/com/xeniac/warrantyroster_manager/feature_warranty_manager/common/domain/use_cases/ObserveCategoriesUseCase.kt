package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.repositories.CategoriesRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ObserveCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke(): Flow<Result<List<WarrantyCategory>, ObserveCategoriesError>> =
        categoriesRepository.observeCategories()
}