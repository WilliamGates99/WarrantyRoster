package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.errors.DeleteWarrantyError

interface DeleteWarrantyRepository {

    suspend fun deleteWarranty(
        warrantyId: String
    ): Result<Unit, DeleteWarrantyError>
}