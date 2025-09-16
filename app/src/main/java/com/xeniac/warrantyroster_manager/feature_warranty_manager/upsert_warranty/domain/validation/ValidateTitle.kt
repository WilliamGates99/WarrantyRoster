package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.validation

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

class ValidateTitle {
    operator fun invoke(
        title: String?
    ): UpsertWarrantyError? {
        if (title.isNullOrBlank()) {
            return UpsertWarrantyError.BlankTitle
        }

        return null
    }
}