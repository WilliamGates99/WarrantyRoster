package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class UpsertingWarranty(
    val title: String,
    val brand: String,
    val model: String,
    val serialNumber: String,
    val description: String,
    val selectedCategory: WarrantyCategory?,
    val isLifetime: Boolean,
    val selectedStartingDate: Instant,
    val selectedExpiryDate: Instant?
)