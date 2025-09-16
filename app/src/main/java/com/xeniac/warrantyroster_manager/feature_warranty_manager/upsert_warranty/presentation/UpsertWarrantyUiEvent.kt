package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation

import com.xeniac.warrantyroster_manager.core.presentation.common.utils.Event
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty

sealed class UpsertWarrantyUiEvent : Event() {
    data class NavigateToWarrantyDetailsScreen(
        val updatedWarranty: Warranty
    ) : UpsertWarrantyUiEvent()
}