package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
            OnboardingPagerItem.entries.getOrNull(index = scrollPosition)?.let { pagerItem ->
                PagerItem(pagerItem = pagerItem)
            }
        }

        PagerButtons(
            pagerState = pagerState,
            onNavigateToAuthScreens = onNavigateToAuthScreens
        )
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