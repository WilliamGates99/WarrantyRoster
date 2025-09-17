package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

class ValidateBrand {
    operator fun invoke(
        brand: String?
    ): UpsertWarrantyError? {
        return null
    }
}