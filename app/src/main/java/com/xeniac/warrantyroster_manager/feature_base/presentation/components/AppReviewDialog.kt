package com.xeniac.warrantyroster_manager.feature_base.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.isAppInstalledFromPlayStore
import com.xeniac.warrantyroster_manager.feature_base.presentation.BaseAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppReviewDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    usePlatformDefaultWidth: Boolean = true,
    decorFitsSystemWindows: Boolean = true,
    securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    dialogProperties: DialogProperties = DialogProperties(
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        usePlatformDefaultWidth = usePlatformDefaultWidth,
        decorFitsSystemWindows = decorFitsSystemWindows,
        securePolicy = securePolicy
    ),
    shape: Shape = AlertDialogDefaults.shape,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    containerColor: Color = AlertDialogDefaults.containerColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    buttonContentColor: Color = MaterialTheme.colorScheme.primary,
    title: String = stringResource(
        id = R.string.base_app_review_dialog_title,
        stringResource(id = R.string.app_name)
    ),
    message: String = stringResource(id = R.string.base_app_review_dialog_message),
    rateNowButtonText: String = stringResource(id = R.string.base_app_review_dialog_btn_rate_now),
    askLaterButtonText: String = stringResource(id = R.string.base_app_review_dialog_btn_remind_later),
    noThanksButtonText: String = stringResource(id = R.string.base_app_review_dialog_btn_never),
    onAction: (action: BaseAction) -> Unit,
    openAppPageInStore: () -> Unit
) {
    if (isVisible) {
        BasicAlertDialog(
            onDismissRequest = { onAction(BaseAction.DismissAppReviewDialog) },
            properties = dialogProperties,
            modifier = modifier
        ) {
            Surface(
                shape = shape,
                tonalElevation = tonalElevation,
                color = containerColor
            ) {
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    DialogTitle(
                        title = title,
                        color = titleContentColor,
                        modifier = Modifier
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            )
                            .align(Alignment.Start)
                    )

                    DialogMessage(
                        message = message,
                        color = textContentColor,
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 24.dp
                            )
                            .align(Alignment.Start)
                    )

                    DialogButtons(
                        color = buttonContentColor,
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    when {
                                        isAppInstalledFromPlayStore() -> onAction(BaseAction.LaunchInAppReview)
                                        else -> openAppPageInStore()
                                    }
                                    onAction(BaseAction.SetSelectedRateAppOptionToNever)
                                    onAction(BaseAction.DismissAppReviewDialog)
                                }
                            ) {
                                Text(
                                    text = rateNowButtonText,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        neutralButton = {
                            TextButton(
                                onClick = {
                                    onAction(BaseAction.SetSelectedRateAppOptionToRemindLater)
                                    onAction(BaseAction.DismissAppReviewDialog)
                                }
                            ) {
                                Text(
                                    text = askLaterButtonText,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    onAction(BaseAction.SetSelectedRateAppOptionToNever)
                                    onAction(BaseAction.DismissAppReviewDialog)
                                }
                            ) {
                                Text(
                                    text = noThanksButtonText,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogTitle(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            text = title,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun DialogMessage(
    message: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            text = message,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun DialogButtons(
    color: Color,
    modifier: Modifier = Modifier,
    confirmButton: @Composable () -> Unit,
    neutralButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalContentColor provides color) {
        Column(modifier = modifier) {
            confirmButton()
            neutralButton()
            dismissButton()
        }
    }
}