package com.xeniac.warrantyroster_manager.feature_auth.login.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showOfflineSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.isWindowWidthSizeCompact
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthUiEvent
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components.CompactScreenWidthLoginContent
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components.MediumScreenWidthLoginContent

@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToForgotPwScreen: () -> Unit,
    onNavigateToBaseScreen: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserverAsEvent(flow = viewModel.loginWithEmailEventChannel) { event ->
        when (event) {
            AuthUiEvent.NavigateToBaseScreen -> onNavigateToBaseScreen()
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(LoginAction.LoginWithEmail) }
            )
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    }

    ObserverAsEvent(flow = viewModel.loginWithGoogleEventChannel) { event ->
        when (event) {
            AuthUiEvent.NavigateToBaseScreen -> onNavigateToBaseScreen()
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(LoginAction.LoginWithGoogle) }
            )
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    }

    ObserverAsEvent(flow = viewModel.loginWithXEventChannel) { event ->
        when (event) {
            AuthUiEvent.NavigateToBaseScreen -> onNavigateToBaseScreen()
            AuthUiEvent.StartActivityForLoginWithX -> {
                val loginTask = viewModel.firebaseAuth.get().startActivityForSignInWithProvider(
                    activity,
                    viewModel.xOAuthProvider.get()
                )
                viewModel.onAction(LoginAction.LoginWithX(loginWithXTask = loginTask))
            }
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(LoginAction.CheckPendingLoginWithX) }
            )
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    }

    ObserverAsEvent(flow = viewModel.loginWithFacebookEventChannel) { event ->
        when (event) {
            AuthUiEvent.NavigateToBaseScreen -> onNavigateToBaseScreen()
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(LoginAction.LoginWithFacebook) }
            )
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    }

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (isWindowWidthSizeCompact()) {
            true -> CompactScreenWidthLoginContent(
                state = state,
                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                onNavigateToForgotPwScreen = onNavigateToForgotPwScreen,
                bottomPadding = innerPadding.calculateBottomPadding(),
                onAction = viewModel::onAction
            )
            false -> MediumScreenWidthLoginContent(
                state = state,
                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                onNavigateToForgotPwScreen = onNavigateToForgotPwScreen,
                bottomPadding = innerPadding.calculateBottomPadding(),
                onAction = viewModel::onAction
            )
        }
    }
}