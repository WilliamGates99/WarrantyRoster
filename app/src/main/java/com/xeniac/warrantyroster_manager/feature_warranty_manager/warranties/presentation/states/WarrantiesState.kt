package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.states

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WarrantiesState(
    val isLoading: Boolean = false,
) : Parcelable