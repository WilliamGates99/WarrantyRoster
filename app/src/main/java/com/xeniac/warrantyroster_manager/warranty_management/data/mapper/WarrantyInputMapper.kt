package com.xeniac.warrantyroster_manager.warranty_management.data.mapper

import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyInputDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput

fun WarrantyInputDto.toWarrantyInput(): WarrantyInput = WarrantyInput(
    title = title,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    lifetime = lifetime,
    startingDate = startingDate,
    expiryDate = expiryDate,
    description = description,
    categoryId = categoryId,
    uuid = uuid
)

fun WarrantyInput.toWarrantyInputDto(): WarrantyInputDto = WarrantyInputDto(
    title = title,
    brand = brand,
    model = model,
    serialNumber = serialNumber,
    lifetime = lifetime,
    startingDate = startingDate,
    expiryDate = expiryDate,
    description = description,
    categoryId = categoryId,
    uuid = uuid
)