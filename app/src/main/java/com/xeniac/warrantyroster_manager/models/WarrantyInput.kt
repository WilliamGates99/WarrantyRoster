package com.xeniac.warrantyroster_manager.models

import androidx.annotation.Keep

@Keep
data class WarrantyInput(
    val title: String,
    val brand: String?,
    val model: String?,
    val serialNumber: String?,
    val startingDate: String,
    val expiryDate: String,
    val description: String?,
    val categoryId: String,
    val uuid: String
)