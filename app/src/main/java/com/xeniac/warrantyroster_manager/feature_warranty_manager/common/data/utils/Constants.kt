package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory

object Constants {

    val MISCELLANEOUS_CATEGORY = WarrantyCategory(
        id = "10",
        title = mapOf(
            "en-US" to "Miscellaneous",
            "en-GB" to "Miscellaneous",
            "fa-IR" to "متفرقه"
        ),
        iconUrl = "https://raw.githubusercontent.com/XeniacDev/warrantyroster/0d57f21fe8f091f73aa8944315d9275dcb7db393/res/warranty_categories/ic_category_miscellaneous.svg"
    )

    // Firestore Categories Collection Fields Constants
    const val CATEGORIES_TITLE = "title"
    const val CATEGORIES_ICON = "icon"

    // Firestore Warranties Collection Fields Constants
    const val WARRANTIES_UUID = "uuid"
    const val WARRANTIES_TITLE = "title"
    const val WARRANTIES_BRAND = "brand"
    const val WARRANTIES_MODEL = "model"
    const val WARRANTIES_SERIAL_NUMBER = "serialNumber"
    const val WARRANTIES_DESCRIPTION = "description"
    const val WARRANTIES_CATEGORY_ID = "categoryId"
    const val WARRANTIES_IS_LIFETIME = "lifetime"
    const val WARRANTIES_STARTING_DATE = "startingDate"
    const val WARRANTIES_EXPIRY_DATE = "expiryDate"
}