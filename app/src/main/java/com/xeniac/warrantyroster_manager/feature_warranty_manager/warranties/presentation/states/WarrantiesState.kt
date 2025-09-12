package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlinx.parcelize.Parcelize

@Parcelize
data class WarrantiesState(
    val categories: List<WarrantyCategory>? = null,
    val warranties: List<Warranty>? = null,
    val errorMessage: UiText? = null,
    val isCategoriesLoading: Boolean = true,
    val isWarrantiesLoading: Boolean = true
) : Parcelable