package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.shimmerEffect

@Composable
fun CategoriesListLoading(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 12.dp
    )
) {
    LazyColumn(
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            count = 100,
            key = { it }
        ) {
            LoadingCategoryItem()
        }
    }
}

@Composable
private fun LoadingCategoryItem(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(shape)
            .shimmerEffect()
    )
}