package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError
import kotlinx.coroutines.flow.Flow

interface WarrantiesRepository {

    fun observeWarranties(
        fetchedCategories: List<WarrantyCategory>?
    ): Flow<Result<List<Warranty>, ObserveWarrantiesError>>

    /**
     *     suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput)
     *
     *     suspend fun deleteWarrantyFromFirestore(warrantyId: String)
     *
     *     suspend fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput)
     *
     *     suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty
     */
}