package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.states

import android.os.Parcelable
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpsertWarrantyState(
    val updatingWarranty: Warranty?,
    val isDataInitialized: Boolean = false,
    val isUpsertLoading: Boolean = false
) : Parcelable