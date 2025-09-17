package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ContentLoadingAnimation(
    isContentLoading: Boolean,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn() + expandIn(),
    exitTransition: ExitTransition = shrinkOut() + fadeOut(),
    loadingContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    AnimatedContent(
        targetState = isContentLoading,
        transitionSpec = { enterTransition.togetherWith(exitTransition) },
        modifier = modifier.fillMaxSize()
    ) { isLoading ->
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            when {
                isLoading -> loadingContent()
                else -> content()
            }
        }
    }
}

@Composable
fun ContentLoadingAnimation(
    isContentLoading: Boolean,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn() + expandIn(),
    exitTransition: ExitTransition = shrinkOut() + fadeOut(),
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = isContentLoading,
        transitionSpec = { enterTransition.togetherWith(exitTransition) },
        modifier = modifier.fillMaxSize()
    ) { isLoading ->
        when {
            isLoading -> LoadingAnimation()
            else -> content()
        }
    }
}

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(all = 32.dp),
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    indicatorTrackColor: Color = Color.Transparent,
    indicatorStrokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CircularProgressIndicator(
            color = indicatorColor,
            trackColor = indicatorTrackColor,
            strokeWidth = indicatorStrokeWidth,
            strokeCap = indicatorStrokeCap
        )
    }
}