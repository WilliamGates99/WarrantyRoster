package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
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
fun MediumScreenWidthPager(
    pagerState: PagerState,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onNavigateToAuthScreens: () -> Unit
) {
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        userScrollEnabled = true,
        modifier = modifier.fillMaxSize()
    ) { scrollPosition ->
        OnboardingPagerItem.entries.getOrNull(
            index = scrollPosition
        )?.let { pagerItem ->
            PagerItem(
                pagerItem = pagerItem,
                pagerState = pagerState,
                onNavigateToAuthScreens = onNavigateToAuthScreens,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun PagerItem(
    pagerItem: OnboardingPagerItem,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        end = 24.dp,
        bottom = 24.dp
    ),
    descriptionStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 24.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = SkyBlue
    ),
    onNavigateToAuthScreens: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .displayCutoutPadding()
            .padding(contentPadding)
    ) {
        Image(
            painter = painterResource(id = pagerItem.imageId),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = stringResource(id = pagerItem.descriptionId),
                style = descriptionStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())
            )

            PagerButtons(
                pagerState = pagerState,
                onNavigateToAuthScreens = onNavigateToAuthScreens
            )
        }
    }
}

@Composable
private fun PagerButtons(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onNavigateToAuthScreens: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        when (val currentPage = pagerState.currentPage) {
            pagerState.pageCount - 1 -> { // Last Page
                StartButton(onClick = onNavigateToAuthScreens)
            }
            0 -> { // First Page
                SkipButton(
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(page = pagerState.pageCount - 1)
                        }
                    }
                )

                NextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(page = 1)
                        }
                    }
                )
            }
            else -> {
                BackButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(page = currentPage - 1)
                        }
                    }
                )

                NextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(page = currentPage + 1)
                        }
                    }
                )
            }
        }
    }
}