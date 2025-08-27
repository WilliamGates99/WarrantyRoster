package com.xeniac.warrantyroster_manager.feature_onboarding.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showShortSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.restartActivity

@Composable
fun OnboardingScreen(
    onNavigateToBaseScreen: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val horizontalPadding by remember { derivedStateOf { 16.dp } }
    val verticalPadding by remember { derivedStateOf { 16.dp } }

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
        topBar = {
            // TODO: LOCALE AND PAGER INDICATOR
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                Text(
//                    text = "Onboarding Screen - Portrait",
                    text = stringResource(id = AppLocale.DEFAULT.titleId),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            else -> {
                Text(
                    text = "Onboarding Screen - Landscape",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
        }
    }
}