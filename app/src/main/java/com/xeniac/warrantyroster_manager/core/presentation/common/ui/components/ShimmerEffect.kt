package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.shimmerEffectColors

@Composable
fun Modifier.shimmerEffect(
    colors: List<Color> = MaterialTheme.colorScheme.shimmerEffectColors,
    duration: Int = 2000,
    repeatMode: RepeatMode = RepeatMode.Reverse,
    isEnabled: Boolean = true
): Modifier = composed {
    val layoutDirection = LocalLayoutDirection.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "transition")

    var initialValue by remember { mutableFloatStateOf(0f) }
    var targetValue by remember { mutableFloatStateOf(0f) }
    var gradientStartOffset by remember { mutableStateOf(Offset.Zero) }
    var gradientEndOffset by remember { mutableStateOf(Offset.Zero) }

    when (layoutDirection) {
        LayoutDirection.Ltr -> {
            initialValue = -size.width.toFloat()
            targetValue = size.width.toFloat()
        }
        LayoutDirection.Rtl -> {
            initialValue = size.width.toFloat()
            targetValue = -size.width.toFloat()
        }
    }

    val startOffsetX by transition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearEasing
            ),
            repeatMode = repeatMode
        ),
        label = "startOffsetX"
    )

    if (layoutDirection == LayoutDirection.Ltr) {
        gradientStartOffset = Offset(
            x = startOffsetX,
            y = size.height.toFloat() / 2f
        )
        gradientEndOffset = Offset(
            x = startOffsetX + size.width.toFloat(),
            y = size.height.toFloat() / 2f
        )
    } else {
        gradientStartOffset = Offset(
            x = startOffsetX + size.width.toFloat(),
            y = size.height.toFloat() / 2f
        )
        gradientEndOffset = Offset(
            x = startOffsetX,
            y = size.height.toFloat() / 2f
        )
    }

    background(
        brush = if (isEnabled) Brush.linearGradient(
            colors = colors,
            start = gradientStartOffset,
            end = gradientEndOffset
        ) else Brush.linearGradient(colors = colors)
    ).onGloballyPositioned {
        size = it.size
    }
}