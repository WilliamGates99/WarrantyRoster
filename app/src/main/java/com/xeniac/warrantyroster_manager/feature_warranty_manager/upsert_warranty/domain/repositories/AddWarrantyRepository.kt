package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertingWarranty

interface AddWarrantyRepository {

    suspend fun addWarranty(
        addingWarranty: UpsertingWarranty
    ): Result<Unit, UpsertWarrantyError>
}