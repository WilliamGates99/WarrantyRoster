package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ObserveWarrantiesUseCase @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository
) {
    operator fun invoke(
        fetchedCategories: List<WarrantyCategory>?
    ): Flow<Result<List<Warranty>, ObserveWarrantiesError>> =
        warrantiesRepository.observeWarranties(fetchedCategories = fetchedCategories)
}