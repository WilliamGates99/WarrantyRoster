package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack

@Composable
fun DeleteWarrantyButton(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.dynamicBlack,
        disabledContentColor = MaterialTheme.colorScheme.dynamicBlack.copy(alpha = 0.38f)
    ),
    icon: Painter = painterResource(id = R.drawable.ic_warranties_delete),
    contentDescription: String = stringResource(id = R.string.warranty_details_content_description_delete),
    iconSize: Dp = 18.dp,
    progressIndicatorColor: Color = colors.contentColor,
    progressIndicatorStrokeWidth: Dp = 2.dp,
    onClick: () -> Unit
) {
    IconButton(
        enabled = !isLoading,
        onClick = onClick,
        colors = colors,
        modifier = modifier
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = progressIndicatorColor,
                    strokeWidth = progressIndicatorStrokeWidth,
                    modifier = Modifier.size(iconSize)
                )
            }
            else -> {
                Icon(
                    painter = icon,
                    contentDescription = contentDescription,
                    modifier = modifier.size(iconSize)
                )
            }
        }
    }
}