package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDarkest
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.WarrantyDetailsAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteWarrantyDialog(
    isVisible: Boolean,
    warrantyTitle: String,
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
    title: String = stringResource(id = R.string.warranty_details_delete_dialog_title),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        textDirection = TextDirection.Content,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    ),
    text: String = stringResource(
        id = R.string.warranty_details_delete_dialog_message,
        warrantyTitle
    ),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        textDirection = TextDirection.Content,
        color = AlertDialogDefaults.textContentColor
    ),
    onAction: (action: WarrantyDetailsAction) -> Unit
) {
    if (isVisible) {
        BasicAlertDialog(
            onDismissRequest = { onAction(WarrantyDetailsAction.DismissDeleteWarrantyDialog) },
            properties = dialogProperties,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(background)
                    .padding(contentPadding)
            ) {
                Text(
                    text = title,
                    style = titleStyle,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = text,
                    style = textStyle,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                DialogButtons(onAction = onAction)
            }
        }
    }
}

@Composable
private fun DialogButtons(
    modifier: Modifier = Modifier,
    onAction: (action: WarrantyDetailsAction) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.End
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        DismissButton(
            onClick = { onAction(WarrantyDetailsAction.DismissDeleteWarrantyDialog) }
        )

        ConfirmButton(
            onClick = {
                onAction(WarrantyDetailsAction.DismissDeleteWarrantyDialog)
                onAction(WarrantyDetailsAction.DeleteWarranty)
            }
        )
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
    text: String = stringResource(
        id = R.string.warranty_details_delete_dialog_btn_delete
    ).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Red
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

@Composable
private fun DismissButton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 4.dp
    ),
    text: String = stringResource(
        id = R.string.warranty_details_delete_dialog_btn_cancel
    ).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicGrayDarkest
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