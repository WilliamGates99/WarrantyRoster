package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
data object WarrantiesScreen

@Serializable
data class UpsertWarrantyScreen(
    // TODO: REPLACE WITH WARRANTY
    val updatingWarrantyId: String? = null
)

@Serializable
data object SettingsScreen