package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyCategoryDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory

fun WarrantyCategoryDto.toWarrantyCategory(): WarrantyCategory = WarrantyCategory(
    id = id,
    title = title,
    iconUrl = iconUrl
)

fun WarrantyCategory.toWarrantyCategoryDto(): WarrantyCategoryDto = WarrantyCategoryDto(
    id = id,
    title = title,
    iconUrl = iconUrl
)