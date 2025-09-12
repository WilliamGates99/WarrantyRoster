package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.compose.ui.text.input.TextFieldValue

sealed interface WarrantiesAction {
    data class SearchQueryChanged(val newValue: TextFieldValue) : WarrantiesAction

    data object GetCategories : WarrantiesAction
    data object GetWarranties : WarrantiesAction
}