package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

sealed interface WarrantiesAction {
    data object ObserveWarranties : WarrantiesAction
}