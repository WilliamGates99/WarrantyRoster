package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.SearchWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class SearchWarrantiesUseCase @Inject constructor(
    private val warrantiesRepository: WarrantiesRepository
) {
    operator fun invoke(
        warranties: List<Warranty>?,
        query: String
    ): Flow<Result<List<Warranty>, SearchWarrantiesError>> = flow {
        return@flow emit(
            warrantiesRepository.searchWarranties(
                warranties = warranties,
                query = query
            )
        )
    }
}