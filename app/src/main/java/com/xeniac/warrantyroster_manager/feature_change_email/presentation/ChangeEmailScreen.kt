package com.xeniac.warrantyroster_manager.feature_change_email.presentation

import androidx.activity.compose.LocalActivity
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.isWindowWidthSizeCompact
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.components.CompactScreenWidthChangeEmailContent
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.components.MediumScreenWidthChangeEmailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(
    userViewModel: UserViewModel,
    onNavigateUp: () -> Unit,
    viewModel: ChangeEmailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    // ObserverAsEvent(flow = viewModel.sendVerificationEmailEventChannel) { event ->
    //     when (event) {
    //         UiEvent.ForceLogoutUnauthorizedUser -> {
    //             userViewModel.onAction(UserAction.Logout)
    //         }
    //         UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
    //             scope = scope,
    //             snackbarHostState = snackbarHostState,
    //             onAction = { viewModel.onAction(SettingsAction.SendVerificationEmail) }
    //         )
    //         is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
    //             message = event.message,
    //             scope = scope,
    //             snackbarHostState = snackbarHostState
    //         )
    //         else -> Unit
    //     }
    // }

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        topBar = {
            CustomCenterAlignedTopAppBar(
                title = stringResource(id = R.string.change_email_app_bar_title),
                scrollBehavior = scrollBehavior,
                onNavigateUpClick = onNavigateUp
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        when (isWindowWidthSizeCompact()) {
            true -> CompactScreenWidthChangeEmailContent(
                state = state,
                innerPadding = innerPadding,
                onAction = viewModel::onAction
            )
            false -> MediumScreenWidthChangeEmailContent(
                state = state,
                innerPadding = innerPadding,
                onAction = viewModel::onAction
            )
        }
    }

    // TODO: SUCCESS DIALOG
}