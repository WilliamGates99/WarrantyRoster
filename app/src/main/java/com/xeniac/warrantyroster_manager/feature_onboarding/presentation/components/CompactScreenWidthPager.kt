package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CompactScreenWidthPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onNavigateToAuthScreens: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { scrollPosition ->
            // TODO: IMPLEMENT
            Text(
                text = "Current page = ${scrollPosition + 1}",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }

        PagerButtons(
            pagerState = pagerState,
            onNavigateToAuthScreens = onNavigateToAuthScreens
        )
    }
}

@Composable
private fun PagerButtons(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    ),
    onNavigateToAuthScreens: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = pagerState.currentPage == pagerState.pageCount - 1,
        transitionSpec = { (fadeIn() + scaleIn()).togetherWith(exit = scaleOut() + fadeOut()) },
        modifier = modifier.fillMaxWidth()
    ) { isLastPage ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            when {
                isLastPage -> StartButton(onClick = onNavigateToAuthScreens)
                else -> {
                    when (val currentPage = pagerState.currentPage) {
                        0 -> SkipButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page = pagerState.pageCount - 1)
                                }
                            }
                        )
                        else -> BackButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page = currentPage - 1)
                                }
                            }
                        )
                    }

                    NextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                            }
                        }
                    )
                }
            }
        }
    }
}