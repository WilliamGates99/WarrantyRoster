package com.xeniac.warrantyroster_manager.feature_settings.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserAction
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.BigButton
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.LocaleBottomSheet
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.bigButtonColors
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showOfflineSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showShortSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.restartActivity
import com.xeniac.warrantyroster_manager.feature_settings.presentation.components.AccountSettingsSection
import com.xeniac.warrantyroster_manager.feature_settings.presentation.components.MiscellaneousSection
import com.xeniac.warrantyroster_manager.feature_settings.presentation.components.SettingsSection
import com.xeniac.warrantyroster_manager.feature_settings.presentation.components.ThemeBottomSheet
import com.xeniac.warrantyroster_manager.feature_settings.presentation.components.VerificationEmailSentDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    bottomPadding: Dp,
    userViewModel: UserViewModel,
    onNavigateToScreen: (destinationScreen: Any) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
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

    ObserverAsEvent(flow = viewModel.setAppLocaleEventChannel) { event ->
        when (event) {
            is UiEvent.RestartActivity -> activity.restartActivity()
            is UiEvent.ShowShortSnackbar -> context.showShortSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            else -> Unit
        }
    }

    ObserverAsEvent(flow = viewModel.setAppThemeEventChannel) { event ->
        when (event) {
            is SettingsUiEvent.UpdateAppTheme -> event.newAppTheme.setAppTheme()
            is UiEvent.ShowShortSnackbar -> context.showShortSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            else -> Unit
        }
    }

    ObserverAsEvent(flow = viewModel.sendVerificationEmailEventChannel) { event ->
        when (event) {
            UiEvent.ForceLogoutUnauthorizedUser -> {
                userViewModel.onAction(UserAction.Logout)
            }
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(SettingsAction.SendVerificationEmail) }
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
                title = stringResource(id = R.string.settings_app_bar_title),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 24.dp),
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
            AccountSettingsSection(
                userProfile = userState.userProfile,
                isUserProfileLoading = userState.isUserProfileLoading,
                isSendVerificationEmailLoading = state.isSendVerificationEmailLoading,
                onAction = viewModel::onAction,
                onNavigateToScreen = onNavigateToScreen
            )

            SettingsSection(
                currentAppLocale = state.currentAppLocale,
                currentAppTheme = state.currentAppTheme,
                onAction = viewModel::onAction
            )

            MiscellaneousSection()

            BigButton(
                isLoading = userState.isLogoutLoading,
                text = stringResource(id = R.string.settings_btn_logout).uppercase(),
                onClick = { userViewModel.onAction(UserAction.Logout) },
                colors = bigButtonColors().copy(
                    containerColor = Red.copy(alpha = 0.10f),
                    contentColor = Red,
                    disabledContainerColor = Red.copy(alpha = 0.10f),
                    disabledContentColor = Red
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    LocaleBottomSheet(
        isVisible = state.isLocaleBottomSheetVisible,
        currentAppLocale = state.currentAppLocale,
        onDismiss = { viewModel.onAction(SettingsAction.DismissLocaleBottomSheet) },
        onLocaleItemClick = { newAppLocale ->
            viewModel.onAction(SettingsAction.SetCurrentAppLocale(newAppLocale = newAppLocale))
        }
    )

    ThemeBottomSheet(
        isVisible = state.isThemeBottomSheetVisible,
        currentAppTheme = state.currentAppTheme,
        onAction = viewModel::onAction
    )

    VerificationEmailSentDialog(
        isVisible = state.isVerificationEmailSentDialogVisible,
        onDismiss = { viewModel.onAction(SettingsAction.DismissVerificationEmailSentDialog) }
    )
}