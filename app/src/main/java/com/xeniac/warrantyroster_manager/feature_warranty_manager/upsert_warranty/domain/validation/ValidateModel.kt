package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

class ValidateModel {
    operator fun invoke(
        model: String?
    ): UpsertWarrantyError? {
        return null
    }
}