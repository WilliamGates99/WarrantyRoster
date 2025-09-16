package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.mappers

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.remote.UpsertingWarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertingWarranty

fun UpsertingWarranty.toUpsertingWarrantyDto(
    uuid: String
): UpsertingWarrantyDto = UpsertingWarrantyDto(
    uuid = uuid,
    title = title,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    description = description,
    categoryId = categoryId,
    isLifetime = isLifetime,
    startingDate = startingDate,
    expiryDate = expiryDate
)