package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantiesScreen(
    bottomPadding: Dp,
    userViewModel: UserViewModel,
    onNavigateToUpsertWarrantyScreen: (warranty: String) -> Unit,
    viewModel: WarrantiesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val horizontalPadding by remember { derivedStateOf { 8.dp } }
    val verticalPadding by remember { derivedStateOf { 24.dp } }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

//    ObserverAsEvent(flow = viewModel.sendVerificationEmailEventChannel) { event ->
//        when (event) {
//            UiEvent.ForceLogoutUnauthorizedUser -> {
//                userViewModel.onAction(UserAction.Logout)
//            }
//            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
//                scope = scope,
//                snackbarHostState = snackbarHostState,
//                onAction = { viewModel.onAction(SettingsAction.SendVerificationEmail) }
//            )
//            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
//                message = event.message,
//                scope = scope,
//                snackbarHostState = snackbarHostState
//            )
//            else -> Unit
//        }
//    }

    Scaffold(
        snackbarHost = {
            SwipeableSnackbar(
                hostState = snackbarHostState,
                modifier = when {
                    bottomPadding > 0.dp -> Modifier.padding(bottom = bottomPadding)
                    else -> Modifier
                }
            )
        },
        topBar = {
            CustomCenterAlignedTopAppBar(
                title = stringResource(id = R.string.warranties_text_title),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        // TODO: ANIMATED CONTENT (SHIMMER EFFECT)
        Column(
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
                    bottom = when {
                        bottomPadding > 0.dp -> bottomPadding
                        else -> 0.dp
                    }
                )
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                )
        ) {
            Text(
                text = "Warranties",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    }
}