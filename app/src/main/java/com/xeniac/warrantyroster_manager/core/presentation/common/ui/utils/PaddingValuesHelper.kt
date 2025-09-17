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
    paddingValues: PaddingValues
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(
            layoutDirection = layoutDirection
        ) + paddingValues.calculateStartPadding(layoutDirection = layoutDirection),
        end = this.calculateEndPadding(
            layoutDirection = layoutDirection
        ) + paddingValues.calculateEndPadding(layoutDirection = layoutDirection),
        top = this.calculateTopPadding() + paddingValues.calculateTopPadding(),
        bottom = this.calculateBottomPadding() + paddingValues.calculateBottomPadding()
    )
}

@Composable
fun PaddingValues.addPaddingValues(
    all: Dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + all,
        end = this.calculateEndPadding(layoutDirection) + all,
        top = this.calculateTopPadding() + all,
        bottom = this.calculateBottomPadding() + all
    )
}

@Composable
fun PaddingValues.addPaddingValues(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + horizontal,
        end = this.calculateEndPadding(layoutDirection) + horizontal,
        top = this.calculateTopPadding() + vertical,
        bottom = this.calculateBottomPadding() + vertical
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
        start = this.calculateStartPadding(layoutDirection) + start,
        end = this.calculateEndPadding(layoutDirection) + end,
        top = this.calculateTopPadding() + top,
        bottom = this.calculateBottomPadding() + bottom
    )
}