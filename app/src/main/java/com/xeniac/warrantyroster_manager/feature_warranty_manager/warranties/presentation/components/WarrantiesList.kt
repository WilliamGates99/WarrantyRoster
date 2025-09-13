package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addPaddingValues
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory

@Composable
fun WarrantiesList(
    warranties: List<Warranty>?,
    filteredWarranties: List<Warranty>?,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    onNavigateToUpsertWarrantyScreen: (warranty: Warranty) -> Unit,
) {
    val horizontalPadding by remember { derivedStateOf { 8.dp } }
    val verticalPadding by remember { derivedStateOf { 8.dp } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = PaddingValues(
            bottom = when {
                bottomPadding > 0.dp -> bottomPadding
                else -> 0.dp
            }
        ).addPaddingValues(
            horizontal = horizontalPadding,
            vertical = verticalPadding
        ),
        modifier = modifier.fillMaxSize()
    ) {
        filteredWarranties?.let { filteredWarranties ->
            items(
                items = filteredWarranties,
                key = { it.id }
            ) { warranty ->
                WarrantyItem(
                    warranty = warranty,
                    onClick = { onNavigateToUpsertWarrantyScreen(warranty) }
                )
            }
            return@LazyColumn
        }

        warranties?.let { warranties ->
            items(
                items = warranties,
                key = { it.id }
            ) { warranty ->
                WarrantyItem(
                    warranty = warranty,
                    onClick = { onNavigateToUpsertWarrantyScreen(warranty) }
                )
            }
        }
    }
}

@Composable
private fun WarrantyItem(
    warranty: Warranty,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 12.dp
    ),
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 2.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding)
    ) {
        CategoryIcon(category = warranty.category)
    }
}

@Composable
private fun CategoryIcon(
    category: WarrantyCategory,
    modifier: Modifier = Modifier,
    currentAppLocale: AppLocale = requireCurrentAppLocale(),
    size: Dp = 52.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.dynamicGrayLight,
    contentPadding: PaddingValues = PaddingValues(all = 12.dp),
    placeholder: Painter = painterResource(id = R.drawable.ic_core_warranty_category_placeholder),
    placeholderColor: Color = Red
) {
    SubcomposeAsyncImage(
        // TODO: TEMP - UNCOMMENT
        // model = category.iconUrl,
        model = "",
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
//            Icon(
//                painter = placeholder,
//                contentDescription = contentDescription,
//                tint = placeholderColor,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(contentPadding)
//            )
            Box(modifier = Modifier.shimmerEffect())
        },
        modifier = modifier
            .size(size)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 2.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
    )
}