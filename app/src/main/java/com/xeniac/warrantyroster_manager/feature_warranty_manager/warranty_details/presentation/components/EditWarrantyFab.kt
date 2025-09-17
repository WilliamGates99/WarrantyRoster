package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White

@Composable
fun EditWarrantyFab(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    icon: Painter = painterResource(id = R.drawable.ic_warranties_fab_edit),
    iconColor: Color = White,
    iconSize: Dp = 24.dp,
    text: String = stringResource(id = R.string.warranty_details_fab_edit).uppercase(),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.SemiBold
    ),
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        expanded = isExpanded,
        shape = shape,
        containerColor = backgroundColor,
        contentColor = iconColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        text = {
            Text(
                text = text,
                style = textStyle
            )
        },
        icon = {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        },
        modifier = modifier.dropShadow(
            shape = shape,
            shadow = Shadow(
                radius = 4.dp,
                color = Black,
                alpha = 0.32f
            )
        )
    )
}