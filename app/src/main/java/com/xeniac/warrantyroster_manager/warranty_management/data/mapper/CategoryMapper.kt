package com.xeniac.warrantyroster_manager.warranty_management.data.mapper

import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.CategoryDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category

fun CategoryDto.toCategory(): Category = Category(
    id = id,
    title = title,
    icon = icon
)

fun Category.toCategoryDto(): CategoryDto = CategoryDto(
    id = id,
    title = title,
    icon = icon
)