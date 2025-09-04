package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showOfflineSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.isWindowWidthSizeCompact
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordAction
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordViewModel
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.components.CompactScreenWidthResetPwInstructionContent
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.components.MediumScreenWidthResetPwInstructionContent

@Composable
fun ResetPwInstructionScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val timerText by viewModel.timerText.collectAsStateWithLifecycle()

    ObserverAsEvent(flow = viewModel.sendResetPasswordEmailEventChannel) { event ->
        when (event) {
            UiEvent.ShowOfflineSnackbar -> context.showOfflineSnackbar(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.onAction(ForgotPasswordAction.SendResetPasswordEmail) }
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
            true -> CompactScreenWidthResetPwInstructionContent(
                state = state,
                timerText = timerText,
                bottomPadding = innerPadding.calculateBottomPadding(),
                onAction = viewModel::onAction,
                onNavigateUp = onNavigateUp
            )
            false -> MediumScreenWidthResetPwInstructionContent(
                state = state,
                timerText = timerText,
                bottomPadding = innerPadding.calculateBottomPadding(),
                onAction = viewModel::onAction,
                onNavigateUp = onNavigateUp
            )
        }
    }
}