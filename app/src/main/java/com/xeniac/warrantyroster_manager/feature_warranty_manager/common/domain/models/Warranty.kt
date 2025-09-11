package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models

import android.os.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Warranty(
    val id: String,
    val title: String,
    val brand: String,
    val model: String,
    val serialNumber: String,
    val description: String,
    val categoryId: String,
    val isLifetime: Boolean,
    val startingDate: LocalDate,
    val expiryDate: LocalDate,
    val itemType: ListItemType = ListItemType.WARRANTY
) : Parcelable

enum class ListItemType {
    WARRANTY,
    AD
}