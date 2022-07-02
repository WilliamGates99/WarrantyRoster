package com.xeniac.warrantyroster_manager.data.remote.models

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.data.remote.models.ListItemType.WARRANTY
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
    val itemType: ListItemType = WARRANTY
) : Parcelable