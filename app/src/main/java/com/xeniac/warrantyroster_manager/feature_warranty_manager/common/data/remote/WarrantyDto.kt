package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarrantyDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String? = null,
    @SerialName("brand")
    val brand: String? = null,
    @SerialName("model")
    val model: String? = null,
    @SerialName("serialNumber")
    val serialNumber: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("categoryId")
    val categoryId: String? = null,
    @SerialName("isLifetime")
    val isLifetime: Boolean? = null,
    @SerialName("startingDate")
    val startingDate: String? = null,
    @SerialName("expiryDate")
    val expiryDate: String? = null
)