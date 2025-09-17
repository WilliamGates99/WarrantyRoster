package com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils

import androidx.compose.foundation.pager.PagerState
import kotlin.math.absoluteValue

/**
 * Calculate the absolute offset for the current page from the scroll position.
 * We use the absolute value which allows us to mirror any effects for both directions
 */
fun PagerState.calculateCurrentPageOffset(
    scrollPosition: Int
): Float {
    return ((currentPage - scrollPosition) + currentPageOffsetFraction).absoluteValue
}