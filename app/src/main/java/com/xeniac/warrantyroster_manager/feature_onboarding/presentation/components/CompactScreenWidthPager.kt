package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.SkyBlue
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.states.OnboardingPagerItem
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
            OnboardingPagerItem.entries.getOrNull(
                index = scrollPosition
            )?.let { pagerItem ->
                PagerItem(pagerItem = pagerItem)
            }
        }

        PagerButtons(
            pagerState = pagerState,
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 12.dp
            ),
            onNavigateToAuthScreens = onNavigateToAuthScreens
        )
    }
}

@Composable
private fun PagerButtons(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn() + scaleIn(),
    exitTransition: ExitTransition = scaleOut() + fadeOut(),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    ),
    onNavigateToAuthScreens: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = pagerState.currentPage == pagerState.pageCount - 1,
        transitionSpec = { (enterTransition).togetherWith(exit = exitTransition) },
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

@Composable
private fun PagerItem(
    pagerItem: OnboardingPagerItem,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    descriptionStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 24.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = SkyBlue
    )
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 100.dp,
            alignment = Alignment.CenterVertically
        ),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        Image(
            painter = painterResource(id = pagerItem.imageId),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Text(
            text = stringResource(id = pagerItem.descriptionId),
            style = descriptionStyle,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        )
    }
}