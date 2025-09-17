package com.xeniac.warrantyroster_manager.feature_base.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White

@Composable
fun GoalFab(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = scaleIn(),
    exitTransition: ExitTransition = scaleOut(),
    size: Dp = 64.dp,
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    icon: Painter = painterResource(id = R.drawable.ic_core_fab_add_warranty),
    iconColor: Color = White,
    iconSize: Dp = 28.dp,
    contentDescription: String = stringResource(id = R.string.base_fab_add_warranty),
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = shape,
            containerColor = backgroundColor,
            contentColor = iconColor,
            modifier = modifier
                .size(size)
                .dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        radius = 4.dp,
                        color = Black,
                        alpha = 0.04f,
                        offset = DpOffset(
                            x = 0.dp,
                            y = 1.dp
                        )
                    )
                )
                .dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        radius = 4.dp,
                        color = Black,
                        alpha = 0.20f,
                        offset = DpOffset(
                            x = 0.dp,
                            y = 2.dp
                        )
                    )
                )
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}