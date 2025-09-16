package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertingWarranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.EditWarrantyRepository
import javax.inject.Inject

class EditWarrantyRepositoryImpl @Inject constructor(

) : EditWarrantyRepository {

    override suspend fun editWarranty(
        editingWarranty: UpsertingWarranty
    ): Result<Unit, UpsertWarrantyError> {
        return Result.Success(Unit)
    }
}