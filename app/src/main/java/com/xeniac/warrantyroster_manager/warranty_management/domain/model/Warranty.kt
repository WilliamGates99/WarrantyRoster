package com.xeniac.warrantyroster_manager.warranty_management.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Warranty(
    val id: String,
    val title: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val serialNumber: String? = null,
    val isLifetime: Boolean? = false,
    val startingDate: String? = null,
    val expiryDate: String? = null,
    val description: String? = null,
    val categoryId: String? = "10",
    val itemType: ListItemType = ListItemType.WARRANTY
) : Parcelable

enum class ListItemType {
    WARRANTY,
    AD
}