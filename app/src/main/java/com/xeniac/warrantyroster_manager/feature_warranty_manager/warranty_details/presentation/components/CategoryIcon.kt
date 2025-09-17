package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireCurrentAppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.shimmerEffect
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray50
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory

@Composable
fun CategoryIcon(
    category: WarrantyCategory,
    modifier: Modifier = Modifier,
    currentAppLocale: AppLocale = requireCurrentAppLocale(),
    size: Dp = 86.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.dynamicGray50,
    contentPadding: PaddingValues = PaddingValues(all = 12.dp),
    placeholder: Painter = painterResource(id = R.drawable.ic_core_warranty_category_placeholder),
    placeholderColor: Color = Red
) {
    SubcomposeAsyncImage(
        model = category.iconUrl,
        contentDescription = category.title[currentAppLocale.languageTag],
        contentScale = ContentScale.Crop,
        success = {
            Icon(
                painter = it.painter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.dynamicBlack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            )
        },
        loading = {
            Box(modifier = Modifier.shimmerEffect())
        },
        error = {
            Icon(
                painter = placeholder,
                contentDescription = contentDescription,
                tint = placeholderColor,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            )
        },
        modifier = modifier
            .size(size)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 12.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
    )
}