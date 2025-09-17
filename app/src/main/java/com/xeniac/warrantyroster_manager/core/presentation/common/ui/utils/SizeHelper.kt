package com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.util.fastRoundToInt

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
fun Dp.toPxInt(): Int = with(LocalDensity.current) { this@toPxInt.toPx().fastRoundToInt() }

@Composable
fun DpSize.toSize(): Size = with(LocalDensity.current) { this@toSize.toSize() }