package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError

data class UpsertWarrantyResult(
    val warrantyIdError: UpsertWarrantyError? = null,
    val titleError: UpsertWarrantyError? = null,
    val brandError: UpsertWarrantyError? = null,
    val modelError: UpsertWarrantyError? = null,
    val serialNumberError: UpsertWarrantyError? = null,
    val descriptionError: UpsertWarrantyError? = null,
    val selectedCategoryError: UpsertWarrantyError? = null,
    val startingAndExpiryDatesError: UpsertWarrantyError? = null,
    val result: Result<Unit, UpsertWarrantyError>? = null
)