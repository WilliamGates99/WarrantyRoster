package com.xeniac.warrantyroster_manager.data.remote.models

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.data.remote.models.ListItemType.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class Warranty(
    val id: String,
    val title: String?,
    val brand: String?,
    val model: String?,
    val serialNumber: String?,
    val isLifetime: Boolean? = false,
    val startingDate: String?,
    val expiryDate: String?,
    val description: String?,
    val categoryId: String? = "10",
    val itemType: ListItemType = WARRANTY
) : Parcelable