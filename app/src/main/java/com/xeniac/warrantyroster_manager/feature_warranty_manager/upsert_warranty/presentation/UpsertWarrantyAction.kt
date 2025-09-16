package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation

import androidx.compose.ui.text.input.TextFieldValue
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory

sealed interface UpsertWarrantyAction {
    data object ShowCategoriesBottomSheet : UpsertWarrantyAction
    data object DismissCategoriesBottomSheet : UpsertWarrantyAction

    data object ShowStartingDatePickerDialog : UpsertWarrantyAction
    data object DismissStartingDatePickerDialog : UpsertWarrantyAction

    data object ShowExpiryDatePickerDialog : UpsertWarrantyAction
    data object DismissExpiryDatePickerDialog : UpsertWarrantyAction

    data class TitleChanged(val newValue: TextFieldValue) : UpsertWarrantyAction
    data class BrandChanged(val newValue: TextFieldValue) : UpsertWarrantyAction
    data class ModelChanged(val newValue: TextFieldValue) : UpsertWarrantyAction
    data class SerialNumberChanged(val newValue: TextFieldValue) : UpsertWarrantyAction
    data class DescriptionChanged(val newValue: TextFieldValue) : UpsertWarrantyAction

    data class SelectedCategoryChanged(val category: WarrantyCategory) : UpsertWarrantyAction

    data class IsLifetimeWarrantyChanged(val isChecked: Boolean) : UpsertWarrantyAction
    data class StartingDateChanged(val startingDateInMs: Long?) : UpsertWarrantyAction
    data class ExpiryDateChanged(val expiryDateInMs: Long?) : UpsertWarrantyAction

    data object GetCategories : UpsertWarrantyAction

    data object AddWarranty : UpsertWarrantyAction
    data object EditWarranty : UpsertWarrantyAction
}