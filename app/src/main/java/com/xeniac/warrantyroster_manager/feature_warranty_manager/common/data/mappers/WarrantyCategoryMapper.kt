package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyCategoryDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun WarrantyCategoryDto.toWarrantyCategory(): WarrantyCategory = WarrantyCategory(
    id = id ?: Uuid.random().toHexString(),
    title = title,
    iconUrl = iconUrl.orEmpty()
)