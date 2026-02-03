package com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PaddingValues.addPaddingValues(
    all: Dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + all.coerceAtLeast(minimumValue = 0.dp),
        end = this.calculateEndPadding(layoutDirection) + all.coerceAtLeast(minimumValue = 0.dp),
        top = this.calculateTopPadding() + all.coerceAtLeast(minimumValue = 0.dp),
        bottom = this.calculateBottomPadding() + all.coerceAtLeast(minimumValue = 0.dp)
    )
}

@Composable
fun PaddingValues.addPaddingValues(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + horizontal.coerceAtLeast(minimumValue = 0.dp),
        end = this.calculateEndPadding(layoutDirection) + horizontal.coerceAtLeast(minimumValue = 0.dp),
        top = this.calculateTopPadding() + vertical.coerceAtLeast(minimumValue = 0.dp),
        bottom = this.calculateBottomPadding() + vertical.coerceAtLeast(minimumValue = 0.dp)
    )
}

@Composable
fun PaddingValues.addPaddingValues(
    start: Dp = 0.dp,
    end: Dp = 0.dp,
    top: Dp = 0.dp,
    bottom: Dp = 0.dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + start.coerceAtLeast(minimumValue = 0.dp),
        end = this.calculateEndPadding(layoutDirection) + end.coerceAtLeast(minimumValue = 0.dp),
        top = this.calculateTopPadding() + top.coerceAtLeast(minimumValue = 0.dp),
        bottom = this.calculateBottomPadding() + bottom.coerceAtLeast(minimumValue = 0.dp)
    )
}

@Composable
fun PaddingValues.addPaddingValues(
    paddingValues: PaddingValues
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(
            layoutDirection = layoutDirection
        ) + paddingValues.calculateStartPadding(
            layoutDirection = layoutDirection
        ).coerceAtLeast(minimumValue = 0.dp),
        end = this.calculateEndPadding(
            layoutDirection = layoutDirection
        ) + paddingValues.calculateEndPadding(
            layoutDirection = layoutDirection
        ).coerceAtLeast(minimumValue = 0.dp),
        top = this.calculateTopPadding() + paddingValues.calculateTopPadding().coerceAtLeast(
            minimumValue = 0.dp
        ),
        bottom = this.calculateBottomPadding() + paddingValues.calculateBottomPadding()
            .coerceAtLeast(
                minimumValue = 0.dp
            )
    )
}