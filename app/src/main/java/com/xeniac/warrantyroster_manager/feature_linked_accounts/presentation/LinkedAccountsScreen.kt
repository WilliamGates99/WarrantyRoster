package com.xeniac.warrantyroster_manager.feature_linked_accounts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel

@Composable
fun LinkedAccountsScreen(
    userViewModel: UserViewModel,
    onNavigateUp: () -> Unit,
    viewModel: LinkedAccountsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

}