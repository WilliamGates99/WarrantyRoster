package com.xeniac.warrantyroster_manager.feature_auth.common.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.LocaleBottomSheet
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showShortSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.isWindowWidthSizeCompact
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.restartActivity
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components.CompactScreenWidthAuthContent
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components.MediumScreenWidthAuthContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    rootNavController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val authNavController = rememberNavController()

    DisposableEffect(key1 = Unit) {
        // Set "windowLightStatusBar" to false
        val windowInsetsController = WindowCompat.getInsetsController(
            /* window = */ activity.window,
            /* view = */ view
        )
        windowInsetsController.isAppearanceLightStatusBars = false

        onDispose {
            // Restore "windowLightStatusBar"
            windowInsetsController.isAppearanceLightStatusBars = !isSystemInDarkTheme
        }
    }

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

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        containerColor = Blue,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (isWindowWidthSizeCompact()) {
            true -> CompactScreenWidthAuthContent(
                currentAppLocale = state.currentAppLocale,
                rootNavController = rootNavController,
                authNavController = authNavController,
                innerPadding = innerPadding,
                onAction = viewModel::onAction
            )
            false -> MediumScreenWidthAuthContent(
                currentAppLocale = state.currentAppLocale,
                rootNavController = rootNavController,
                authNavController = authNavController,
                innerPadding = innerPadding,
                onAction = viewModel::onAction
            )
        }
    }

    LocaleBottomSheet(
        isVisible = state.isLocaleBottomSheetVisible,
        currentAppLocale = state.currentAppLocale ?: AppLocale.DEFAULT,
        onDismiss = { viewModel.onAction(AuthAction.DismissLocaleBottomSheet) },
        onLocaleItemClick = { newAppLocale ->
            viewModel.onAction(AuthAction.SetCurrentAppLocale(newAppLocale = newAppLocale))
        }
    )
}