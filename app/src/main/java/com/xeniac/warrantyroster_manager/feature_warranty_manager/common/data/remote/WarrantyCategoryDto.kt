package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.LANGUAGE_TAG
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.TITLE

@Keep
data class WarrantyCategoryDto(
    @get:Exclude
    val id: String? = null,
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: Map<LANGUAGE_TAG, TITLE> = emptyMap(),
    @get:PropertyName("icon")
    @set:PropertyName("icon")
    var iconUrl: String? = null
)