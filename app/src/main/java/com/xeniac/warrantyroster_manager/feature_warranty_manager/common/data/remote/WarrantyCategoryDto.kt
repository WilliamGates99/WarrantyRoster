package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class WarrantyCategoryDto(
    val id: String,
    val title: Map<String, String>,
    val iconUrl: String
)