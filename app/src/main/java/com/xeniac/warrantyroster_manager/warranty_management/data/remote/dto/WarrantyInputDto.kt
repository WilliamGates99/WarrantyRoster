package com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto

import androidx.annotation.Keep

@Keep
data class WarrantyInputDto(
    val title: String,
    val brand: String?,
    val model: String?,
    val serialNumber: String?,
    val lifetime: Boolean = false,
    val startingDate: String,
    val expiryDate: String?,
    val description: String?,
    val categoryId: String = "10",
    val uuid: String
)