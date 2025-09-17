package com.xeniac.warrantyroster_manager.feature_change_password.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserAction
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showOfflineSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.isWindowWidthSizeCompact
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.components.CompactScreenWidthChangePasswordContent
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.components.MediumScreenWidthChangePasswordContent
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.components.PasswordChangedDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    userViewModel: UserViewModel,
    onNavigateUp: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserverAsEvent(flow = viewModel.changeUserPasswordEventChannel) { event ->
        when (event) {
            UiEvent.ForceLogoutUnauthorizedUser -> {
                userViewModel.onAction(UserAction.Logout)
            }
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(ChangePasswordAction.ChangeUserPassword) }
            )
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        topBar = {
            CustomCenterAlignedTopAppBar(
                title = stringResource(id = R.string.change_password_app_bar_title),
                scrollBehavior = scrollBehavior,
                onNavigateUpClick = onNavigateUp
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        when (isWindowWidthSizeCompact()) {
            true -> CompactScreenWidthChangePasswordContent(
                state = state,
                innerPadding = innerPadding,
                onAction = viewModel::onAction
            )
            false -> MediumScreenWidthChangePasswordContent(
                state = state,
                innerPadding = innerPadding,
                onAction = viewModel::onAction
            )
        }
    }

    PasswordChangedDialog(
        isVisible = state.isPasswordChangedDialogVisible,
        onNavigateUp = onNavigateUp,
        onDismiss = { viewModel.onAction(ChangePasswordAction.DismissPasswordChangedDialog) }
    )
}