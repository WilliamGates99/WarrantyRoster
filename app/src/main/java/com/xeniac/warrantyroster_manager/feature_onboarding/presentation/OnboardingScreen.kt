package com.xeniac.warrantyroster_manager.feature_onboarding.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.LocaleBottomSheet
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showShortSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.restartActivity
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components.CompactScreenWidthPager
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components.MediumScreenWidthPager
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components.OnboardingTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onNavigateToAuthScreen: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { 4 })

    DisposableEffect(key1 = Unit) {
        // Set "windowLightStatusBar" to false
        val windowInsetsController = WindowCompat.getInsetsController(
            /* window = */ activity.window,
            /* view = */ view
        )
        windowInsetsController.isAppearanceLightStatusBars = false

        onDispose {}
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

    BackHandler(
        enabled = pagerState.settledPage != 0,
        onBack = {
            scope.launch {
                pagerState.animateScrollToPage(page = pagerState.settledPage - 1)
            }
        }
    )

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        topBar = {
            OnboardingTopBar(
                currentAppLocale = state.currentAppLocale,
                pagerState = pagerState,
                onAction = viewModel::onAction
            )
        },
        containerColor = Blue,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> CompactScreenWidthPager(
                pagerState = pagerState,
                onNavigateToAuthScreen = onNavigateToAuthScreen,
                modifier = Modifier.padding(innerPadding)
            )
            else -> MediumScreenWidthPager(
                pagerState = pagerState,
                innerPadding = innerPadding,
                onNavigateToAuthScreen = onNavigateToAuthScreen
            )
        }
    }

    LocaleBottomSheet(
        isVisible = state.isLocaleBottomSheetVisible,
        currentAppLocale = state.currentAppLocale ?: AppLocale.DEFAULT,
        onDismiss = { viewModel.onAction(OnboardingAction.DismissLocaleBottomSheet) },
        onLocaleItemClick = { newAppLocale ->
            viewModel.onAction(OnboardingAction.SetCurrentAppLocale(newAppLocale = newAppLocale))
        }
    )
}