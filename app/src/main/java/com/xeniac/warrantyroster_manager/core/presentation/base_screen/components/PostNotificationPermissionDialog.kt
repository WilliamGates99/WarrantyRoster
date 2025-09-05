package com.xeniac.warrantyroster_manager.core.presentation.base_screen.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.base_screen.BaseAction
import com.xeniac.warrantyroster_manager.core.presentation.base_screen.utils.PostNotificationPermissionHelper
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.PermissionDialog
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.openAppSettings

@Composable
fun PostNotificationPermissionHandler(
    isPermissionDialogVisible: Boolean,
    permissionDialogQueue: List<String>,
    modifier: Modifier = Modifier,
    onAction: (action: BaseAction) -> Unit
) {
    val isRunningAndroid13OrNewer by remember {
        derivedStateOf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }
    }

    @SuppressLint("InlinedApi")
    if (isRunningAndroid13OrNewer) {
        val context = LocalContext.current
        val activity = LocalActivity.current ?: context.findActivity()

        val postNotificationPermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onAction(
                BaseAction.OnPermissionResult(
                    permission = Manifest.permission.POST_NOTIFICATIONS,
                    isGranted = isGranted
                )
            )
        }

        LaunchedEffect(key1 = Unit) {
            postNotificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        NotificationPermissionDialog(
            activity = activity,
            isVisible = isPermissionDialogVisible,
            permissionQueue = permissionDialogQueue,
            onConfirmClick = {
                postNotificationPermissionResultLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            },
            onDismiss = { permission ->
                onAction(BaseAction.DismissPermissionDialog(permission))
            },
            modifier = modifier
        )
    }
}

@Composable
private fun NotificationPermissionDialog(
    activity: Activity,
    isVisible: Boolean,
    permissionQueue: List<String>,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit,
    onDismiss: (permission: String) -> Unit
) {
    if (isVisible) {
        permissionQueue.reversed().forEach { permission ->
            PermissionDialog(
                icon = painterResource(id = R.drawable.ic_core_dialog_post_notification),
                permissionHelper = when (permission) {
                    Manifest.permission.POST_NOTIFICATIONS -> PostNotificationPermissionHelper()
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    /* activity = */ activity,
                    /* permission = */ permission
                ),
                onConfirmClick = onConfirmClick,
                onOpenAppSettingsClick = activity::openAppSettings,
                onDismiss = { onDismiss(permission) },
                modifier = modifier
            )
        }
    }
}