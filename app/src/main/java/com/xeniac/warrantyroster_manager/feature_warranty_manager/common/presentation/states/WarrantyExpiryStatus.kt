package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.states

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Orange
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red

enum class WarrantyExpiryStatus(
    @param:StringRes val titleId: Int,
    @param:StringRes val descriptionId: Int,
    val color: Color
) {
    VALID(
        titleId = R.string.warranties_expiration_status_valid_title,
        descriptionId = R.string.warranties_expiration_status_valid_description,
        color = Green
    ),
    SOON(
        titleId = R.string.warranties_expiration_status_soon_title,
        descriptionId = R.string.warranties_expiration_status_soon_description,
        color = Orange
    ),
    EXPIRED(
        titleId = R.string.warranties_expiration_status_expired_title,
        descriptionId = R.string.warranties_expiration_status_expired_description,
        color = Red
    )
}