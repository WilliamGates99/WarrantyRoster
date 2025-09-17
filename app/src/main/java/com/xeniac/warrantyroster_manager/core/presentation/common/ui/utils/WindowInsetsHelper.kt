@file:OptIn(ExperimentalLayoutApi::class)

package com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun getStatusBarHeight(): Int = WindowInsets.statusBarsIgnoringVisibility.getTop(
    density = LocalDensity.current
)

@Composable
fun getNavigationBarHeight(): Int = WindowInsets.navigationBarsIgnoringVisibility.getBottom(
    density = LocalDensity.current
)

@Composable
fun getStatusBarHeightDp(): Dp = getStatusBarHeight().toDp()

@Composable
fun getNavigationBarHeightDp(): Dp = getNavigationBarHeight().toDp()