package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

@Keep
data class WarrantyDto(
    @get:Exclude
    val id: String? = null,
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String? = null,
    @get:PropertyName("brand")
    @set:PropertyName("brand")
    var brand: String? = null,
    @get:PropertyName("model")
    @set:PropertyName("model")
    var model: String? = null,
    @get:PropertyName("serialNumber")
    @set:PropertyName("serialNumber")
    var serialNumber: String? = null,
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,
    @get:PropertyName("categoryId")
    @set:PropertyName("categoryId")
    var categoryId: String? = null,
    @get:PropertyName("isLifetime")
    @set:PropertyName("isLifetime")
    var isLifetime: Boolean? = null,
    @get:PropertyName("startingDate")
    @set:PropertyName("startingDate")
    var startingDate: String? = null,
    @get:PropertyName("expiryDate")
    @set:PropertyName("expiryDate")
    var expiryDate: String? = null
)