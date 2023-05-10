package com.xeniac.warrantyroster_manager.warranty_management.data.mapper

import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty

fun WarrantyDto.toWarranty(): Warranty = Warranty(
    id = id,
    title = title,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    isLifetime = isLifetime,
    startingDate = startingDate,
    expiryDate = expiryDate,
    description = description,
    categoryId = categoryId
)

fun Warranty.toWarrantyDto(): WarrantyDto = WarrantyDto(
    id = id,
    title = title,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    isLifetime = isLifetime,
    startingDate = startingDate,
    expiryDate = expiryDate,
    description = description,
    categoryId = categoryId
)