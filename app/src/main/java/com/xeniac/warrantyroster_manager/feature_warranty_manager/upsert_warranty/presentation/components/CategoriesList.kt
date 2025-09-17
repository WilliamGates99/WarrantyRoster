package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireCurrentAppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.shimmerEffect
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.UpsertWarrantyAction

@Composable
fun CategoriesList(
    categories: List<WarrantyCategory>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 12.dp
    ),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth()
    ) {
        items(
            items = categories,
            key = { it.id }
        ) { category ->
            CategoryItem(
                category = category,
                onClick = {
                    onAction(UpsertWarrantyAction.SelectedCategoryChanged(category = category))
                    onAction(UpsertWarrantyAction.DismissCategoriesBottomSheet)
                }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: WarrantyCategory,
    modifier: Modifier = Modifier,
    currentAppLocale: AppLocale = requireCurrentAppLocale(),
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 12.dp
    ),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    ),
    title: String? = category.title[currentAppLocale.languageTag],
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.Start
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .clickable(
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(contentPadding)
    ) {
        CategoryIcon(
            iconUrl = category.iconUrl,
            contentDescription = title
        )

        Text(
            text = title.orEmpty(),
            style = titleStyle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryIcon(
    iconUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    background: Color = MaterialTheme.colorScheme.dynamicGrayLight,
    contentPadding: PaddingValues = PaddingValues(all = 8.dp),
    placeholder: Painter = painterResource(id = R.drawable.ic_core_warranty_category_placeholder),
    placeholderColor: Color = Red
) {
    SubcomposeAsyncImage(
        model = iconUrl,
        contentDescription = contentDescription,
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
                    radius = 4.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
    )
}