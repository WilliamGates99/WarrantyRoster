package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class WarrantyCategory(
    val id: String,
    val title: Map<String, String>,
    val iconUrl: String
) : Parcelable