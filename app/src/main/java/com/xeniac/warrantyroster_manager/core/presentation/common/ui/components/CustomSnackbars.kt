package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayLightDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.GrayLightLight
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SwipeableSnackbar(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    dismissSnackbarState: SwipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                hostState.currentSnackbarData?.dismiss()
                true
            } else false
        }
    )
) {
    val layoutDirection = LocalLayoutDirection.current

    // Set the layout direction to LTR to solve the opposite swipe direction in RTL layouts
    CompositionLocalProvider(value = LocalLayoutDirection provides LayoutDirection.Ltr) {
        LaunchedEffect(dismissSnackbarState.currentValue) {
            if (dismissSnackbarState.currentValue != SwipeToDismissBoxValue.Settled) {
                dismissSnackbarState.reset()
            }
        }

        SwipeToDismissBox(
            state = dismissSnackbarState,
            backgroundContent = {},
            modifier = modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(value = LocalLayoutDirection provides layoutDirection) {
                SnackbarHost(
                    hostState = hostState,
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            actionColor = if (isSystemInDarkTheme()) GrayLightDark else GrayLightLight,
                            actionContentColor = if (isSystemInDarkTheme()) GrayLightDark else GrayLightLight
                        )
                    }
                )
            }
        }
    }
}

fun Context.showOfflineSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: UiText = UiText.StringResource(R.string.error_network_connection_unavailable),
    actionLabel: UiText = UiText.StringResource(R.string.error_btn_retry),
    onAction: () -> Unit,
    onDismiss: () -> Unit = {}
) = scope.launch {
    snackbarHostState.currentSnackbarData?.dismiss()

    val result = snackbarHostState.showSnackbar(
        message = message.asString(context = this@showOfflineSnackbar),
        actionLabel = actionLabel.asString(context = this@showOfflineSnackbar),
        duration = SnackbarDuration.Indefinite
    )

    when (result) {
        SnackbarResult.ActionPerformed -> onAction()
        SnackbarResult.Dismissed -> onDismiss()
    }
}

fun Context.showIntentAppNotFoundSnackbar(
    isVisible: Boolean,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: UiText = UiText.StringResource(R.string.error_intent_app_not_found),
    onDismiss: () -> Unit
) {
    if (isVisible) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = message.asString(context = this@showIntentAppNotFoundSnackbar),
                duration = SnackbarDuration.Short
            )

            when (result) {
                SnackbarResult.ActionPerformed -> Unit
                SnackbarResult.Dismissed -> onDismiss()
            }
        }
    }
}

fun Context.showShortSnackbar(
    message: UiText,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit = {}
) = scope.launch {
    snackbarHostState.currentSnackbarData?.dismiss()

    val result = snackbarHostState.showSnackbar(
        message = message.asString(context = this@showShortSnackbar),
        duration = SnackbarDuration.Short
    )

    when (result) {
        SnackbarResult.ActionPerformed -> Unit
        SnackbarResult.Dismissed -> onDismiss()
    }
}

fun Context.showLongSnackbar(
    message: UiText,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit = {}
) = scope.launch {
    snackbarHostState.currentSnackbarData?.dismiss()

    val result = snackbarHostState.showSnackbar(
        message = message.asString(context = this@showLongSnackbar),
        duration = SnackbarDuration.Long
    )

    when (result) {
        SnackbarResult.ActionPerformed -> Unit
        SnackbarResult.Dismissed -> onDismiss()
    }
}

fun Context.showActionSnackbar(
    message: UiText,
    actionLabel: UiText,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onAction: () -> Unit,
    onDismiss: () -> Unit = {}
) = scope.launch {
    snackbarHostState.currentSnackbarData?.dismiss()

    val result = snackbarHostState.showSnackbar(
        message = message.asString(context = this@showActionSnackbar),
        actionLabel = actionLabel.asString(context = this@showActionSnackbar),
        duration = SnackbarDuration.Indefinite
    )

    when (result) {
        SnackbarResult.ActionPerformed -> onAction()
        SnackbarResult.Dismissed -> onDismiss()
    }
}