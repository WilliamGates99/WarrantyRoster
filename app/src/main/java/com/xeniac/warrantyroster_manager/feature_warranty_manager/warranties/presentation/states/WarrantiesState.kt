package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class WarrantiesState(
    val categories: List<WarrantyCategory>? = null,
    val warranties: List<Warranty>? = null,
    val filteredWarranties: List<Warranty>? = null,
    val searchQueryState: CustomTextFieldState = CustomTextFieldState(),
    val errorMessage: UiText? = null,
    val searchErrorMessage: UiText? = null,
    val isSearchBarVisible: Boolean = false,
    val isCategoriesLoading: Boolean = true,
    val isWarrantiesLoading: Boolean = true,
    val isSearchWarrantiesLoading: Boolean = false
) : Parcelable