package com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto

data class WarrantyDto(
    val id: String,
    val title: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val serialNumber: String? = null,
    val isLifetime: Boolean? = false,
    val startingDate: String? = null,
    val expiryDate: String? = null,
    val description: String? = null,
    val categoryId: String? = "10"
)