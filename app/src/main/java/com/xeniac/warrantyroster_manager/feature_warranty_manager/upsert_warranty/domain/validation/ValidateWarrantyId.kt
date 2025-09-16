package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

class ValidateWarrantyId {
    operator fun invoke(
        warrantyId: String?
    ): UpsertWarrantyError? {
        if (warrantyId.isNullOrBlank()) {
            return UpsertWarrantyError.InvalidWarranty
        }

        return null
    }
}