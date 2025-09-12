package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import kotlinx.serialization.Serializable

@Serializable
data object WarrantiesScreen

@Serializable
data class WarrantyDetailsScreen(
    val warranty: Warranty
)

@Serializable
data class UpsertWarrantyScreen(
    val updatingWarranty: Warranty? = null
)