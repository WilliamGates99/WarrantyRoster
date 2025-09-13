package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.states

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Orange
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red

enum class WarrantyExpiryStatus(
    @param:StringRes val titleId: Int,
    val color: Color
) {
    VALID(
        titleId = R.string.warranties_expiration_status_valid,
        color = Green
    ),
    SOON(
        titleId = R.string.warranties_expiration_status_soon,
        color = Orange
    ),
    EXPIRED(
        titleId = R.string.warranties_expiration_status_expired,
        color = Red
    )
}