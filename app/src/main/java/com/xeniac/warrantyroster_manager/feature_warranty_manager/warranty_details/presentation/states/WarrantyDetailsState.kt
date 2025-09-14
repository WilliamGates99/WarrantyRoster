package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.states

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty

data class WarrantyDetailsState(
    val warranty: Warranty,
    val isDeleteLoading: Boolean = false
)