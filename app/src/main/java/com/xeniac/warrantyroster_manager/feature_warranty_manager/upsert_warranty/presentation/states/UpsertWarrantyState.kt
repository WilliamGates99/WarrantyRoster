package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlinx.parcelize.Parcelize
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Parcelize
data class UpsertWarrantyState(
    val updatingWarranty: Warranty?,
    val isUpdatingWarrantyDataSet: Boolean = false,
    val categories: List<WarrantyCategory>? = null,
    val categoriesErrorMessage: UiText? = null,
    val titleState: CustomTextFieldState = CustomTextFieldState(),
    val brandState: CustomTextFieldState = CustomTextFieldState(),
    val modelState: CustomTextFieldState = CustomTextFieldState(),
    val serialNumberState: CustomTextFieldState = CustomTextFieldState(),
    val descriptionState: CustomTextFieldState = CustomTextFieldState(),
    val selectedCategory: WarrantyCategory? = null,
    val selectedCategoryError: UiText? = null,
    val isLifetimeWarranty: Boolean = false,
    val selectedStartingDate: Instant? = null,
    val selectedExpiryDate: Instant? = null,
    val selectedStartingAndExpiryDatesError: UiText? = null,
    val isWarrantiesBottomSheetVisible: Boolean = false,
    val isStartingDatePickerDialogVisible: Boolean = false,
    val isExpiryDatePickerDialogVisible: Boolean = false,
    val isCategoriesLoading: Boolean = true,
    val isUpsertLoading: Boolean = false
) : Parcelable