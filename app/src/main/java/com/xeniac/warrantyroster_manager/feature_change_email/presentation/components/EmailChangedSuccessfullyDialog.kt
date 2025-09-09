package com.xeniac.warrantyroster_manager.feature_change_email.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.xeniac.warrantyroster_manager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailChangedSuccessfullyDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    dialogProperties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = true,
        securePolicy = SecureFlagPolicy.Inherit
    ),
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 20.dp,
        vertical = 16.dp
    ),
    message: String = stringResource(id = R.string.change_email_success_dialog_message),
    messageStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        color = AlertDialogDefaults.textContentColor
    ),
    onDismiss: () -> Unit
) {
    if (isVisible) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
            properties = dialogProperties,
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(space = 20.dp),
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(background)
                    .padding(contentPadding)
            ) {
                Text(
                    text = message,
                    style = messageStyle,
                    modifier = Modifier.fillMaxWidth()
                )

                ConfirmButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ConfirmButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(id = R.string.change_email_success_dialog_btn_confirm).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    ),
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = textStyle,
        modifier = modifier
            .clip(shape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = textStyle.color)
            )
            .padding(contentPadding)
    )
}