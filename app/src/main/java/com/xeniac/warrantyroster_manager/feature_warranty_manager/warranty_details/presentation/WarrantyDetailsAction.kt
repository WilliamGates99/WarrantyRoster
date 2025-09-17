package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation

sealed interface WarrantyDetailsAction {
    data object ShowDeleteWarrantyDialog : WarrantyDetailsAction
    data object DismissDeleteWarrantyDialog : WarrantyDetailsAction

    data object DeleteWarranty : WarrantyDetailsAction
}