package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.remote

import com.google.firebase.firestore.PropertyName

data class UpsertingWarrantyDto(
    @get:PropertyName("uuid")
    val uuid: String,
    @get:PropertyName("title")
    val title: String,
    @get:PropertyName("brand")
    val brand: String? = null,
    @get:PropertyName("model")
    val model: String? = null,
    @get:PropertyName("serialNumber")
    val serialNumber: String? = null,
    @get:PropertyName("description")
    val description: String? = null,
    @get:PropertyName("categoryId")
    val categoryId: String = "10",
    @get:PropertyName("lifetime")
    val isLifetime: Boolean = false,
    @get:PropertyName("startingDate")
    val startingDate: String,
    @get:PropertyName("expiryDate")
    val expiryDate: String?
)