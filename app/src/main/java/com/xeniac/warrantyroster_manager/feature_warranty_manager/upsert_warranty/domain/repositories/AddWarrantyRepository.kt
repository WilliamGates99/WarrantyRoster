package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

interface AddWarrantyRepository {

    suspend fun addWarranty(): Result<Unit, UpsertWarrantyError>
}