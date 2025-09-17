package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.SearchWarrantiesError
import kotlinx.coroutines.flow.Flow

interface WarrantiesRepository {

    fun observeWarranties(
        fetchedCategories: List<WarrantyCategory>?
    ): Flow<Result<List<Warranty>, ObserveWarrantiesError>>

    suspend fun searchWarranties(
        warranties: List<Warranty>?,
        query: String,
        delayInMillis: Long = 500L
    ): Result<List<Warranty>, SearchWarrantiesError>
}