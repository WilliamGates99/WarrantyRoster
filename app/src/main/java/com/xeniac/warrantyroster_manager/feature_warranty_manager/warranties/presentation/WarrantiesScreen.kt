package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel

@Composable
fun WarrantiesScreen(
    bottomPadding: Dp,
    userViewModel: UserViewModel,
    onNavigateToUpsertWarrantyScreen: (warranty: String) -> Unit,
    viewModel: WarrantiesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

}