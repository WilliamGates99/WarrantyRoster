package com.xeniac.warrantyroster_manager.model

import androidx.annotation.Keep

@Keep
data class WarrantyInput(
    var title: String,
    var brand: String?,
    var model: String?,
    var serialNumber: String?,
    var startingDate: String,
    var expiryDate: String,
    var description: String?,
    var categoryId: String,
    var uuid: String
)