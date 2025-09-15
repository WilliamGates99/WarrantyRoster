package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import kotlinx.datetime.LocalDate

@Composable
fun HeaderSection(
    category: WarrantyCategory,
    isLifetime: Boolean,
    startingDate: LocalDate,
    expiryDate: LocalDate,
    modifier: Modifier = Modifier
) {

}