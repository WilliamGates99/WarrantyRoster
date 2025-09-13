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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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