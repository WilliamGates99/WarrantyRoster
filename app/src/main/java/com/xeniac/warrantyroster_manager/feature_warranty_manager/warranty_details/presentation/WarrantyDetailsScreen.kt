package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.UserAction
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongToast
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showOfflineSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components.DeleteWarrantyButton
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components.DeleteWarrantyDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyDetailsScreen(
    userViewModel: UserViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToUpsertWarrantyScreen: (warranty: Warranty) -> Unit,
    viewModel: WarrantyDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val horizontalPadding by remember { derivedStateOf { 8.dp } }
    val verticalPadding by remember { derivedStateOf { 16.dp } }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserverAsEvent(flow = viewModel.deleteWarrantyEventChannel) { event ->
        when (event) {
            UiEvent.NavigateUp -> onNavigateUp()
            UiEvent.ForceLogoutUnauthorizedUser -> {
                userViewModel.onAction(UserAction.Logout)
            }
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(WarrantyDetailsAction.DeleteWarranty) }
            )
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            is UiEvent.ShowLongToast -> context.showLongToast(message = event.message)
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        topBar = {
            CustomCenterAlignedTopAppBar(
                title = state.warranty.title,
                scrollBehavior = scrollBehavior,
                onNavigateUpClick = onNavigateUp,
                actions = {
                    DeleteWarrantyButton(
                        isLoading = state.isDeleteLoading,
                        onAction = viewModel::onAction
                    )
                }
            )
        },
        floatingActionButton = {
            // TODO: EDIT FAB
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 12.dp),
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                )
        ) {
            // TODO: WARRANTY DETAILS
        }
    }

    DeleteWarrantyDialog(
        isVisible = state.isDeleteWarrantyDialogVisible,
        warrantyTitle = state.warranty.title,
        onAction = viewModel::onAction
    )
}