package com.xeniac.warrantyroster_manager.feature_onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.SkyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.calculateCurrentPageOffset
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toDp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toPx

@Composable
fun ExpandingDotIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    count: Int = pagerState.pageCount,
    activeIndicatorWidth: Dp = 12.dp,
    inactiveIndicatorWidth: Dp = 4.dp,
    indicatorHeight: Dp = 4.dp,
    indicatorShape: Shape = CircleShape,
    indicatorColor: Color = SkyBlue,
    indicatorsSpacing: Dp = 4.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = indicatorsSpacing,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        val spacing = indicatorsSpacing.toPx()
        val dotWidth = inactiveIndicatorWidth.toPx()

        val activeDotWidth = activeIndicatorWidth.toPx()
        var x = 0f

        repeat(times = count) { index ->
            val positionOffset = pagerState.calculateCurrentPageOffset(scrollPosition = 0)
            val dotOffset = positionOffset % 1
            val current = positionOffset.toInt()

            val factor = (dotOffset * (activeDotWidth - dotWidth))

            val calculatedWidth = when {
                index == current -> activeDotWidth - factor
                index - 1 == current || (index == 0 && positionOffset > count - 1) -> dotWidth + factor
                else -> dotWidth
            }

            Box(
                modifier = Modifier
                    .width(calculatedWidth.toDp())
                    .height(indicatorHeight)
                    .clip(indicatorShape)
                    .background(indicatorColor)
            )

            x += calculatedWidth + spacing
        }
    }
}