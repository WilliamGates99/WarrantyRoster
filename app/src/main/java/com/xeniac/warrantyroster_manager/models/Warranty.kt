package com.xeniac.warrantyroster_manager.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Warranty(
    val id: String,
    val title: String?,
    val brand: String?,
    val model: String?,
    val serialNumber: String?,
    val startingDate: String?,
    val expiryDate: String?,
    val description: String?,
    val categoryId: String?,
    val itemType: ListItemType
) : Parcelable