package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

typealias LANGUAGE_TAG = String
typealias TITLE = String

@Serializable
@Parcelize
data class WarrantyCategory(
    val id: String,
    val title: Map<LANGUAGE_TAG, TITLE>,
    val iconUrl: String
) : Parcelable