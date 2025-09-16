package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayLight
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicNavyBlue
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addPaddingValues
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.states.WarrantyExpiryStatus
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.presentation.utils.calculateWarrantyExpiryStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
fun WarrantiesList(
    warranties: List<Warranty>?,
    filteredWarranties: List<Warranty>?,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    onNavigateToWarrantyDetailsScreen: (warranty: Warranty) -> Unit,
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
                    onClick = { onNavigateToWarrantyDetailsScreen(warranty) }
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
                    onClick = { onNavigateToWarrantyDetailsScreen(warranty) }
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
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
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

        WarrantyInfo(
            warranty = warranty,
            modifier = Modifier.weight(1f)
        )
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
                    radius = 4.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
    )
}

@Composable
private fun WarrantyInfo(
    warranty: Warranty,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        TitleAndExpiryStatus(
            title = warranty.title,
            isLifetime = warranty.isLifetime,
            expiryDate = warranty.expiryDate
        )

        ExpiryDateAndCategoryTitle(
            isLifetime = warranty.isLifetime,
            expiryDate = warranty.expiryDate,
            category = warranty.category
        )
    }
}

@Composable
private fun TitleAndExpiryStatus(
    title: String,
    isLifetime: Boolean,
    expiryDate: LocalDate?,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.dynamicNavyBlue
    ),
    titleMaxLines: Int = 1,
    titleOverflow: TextOverflow = TextOverflow.Ellipsis
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = titleStyle,
            maxLines = titleMaxLines,
            overflow = titleOverflow,
            modifier = Modifier.weight(1f)
        )

        ExpiryStatus(
            expiryStatus = calculateWarrantyExpiryStatus(
                isLifetime = isLifetime,
                expiryDate = expiryDate
            )
        )
    }
}

@Composable
private fun ExpiryStatus(
    expiryStatus: WarrantyExpiryStatus,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Black,
        color = expiryStatus.color
    ),
    maxLines: Int = 1
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = expiryStatus.titleId).uppercase(),
            style = textStyle,
            maxLines = maxLines
        )

        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(expiryStatus.color)
        )
    }
}

@Composable
private fun ExpiryDateAndCategoryTitle(
    isLifetime: Boolean,
    expiryDate: LocalDate?,
    category: WarrantyCategory,
    modifier: Modifier = Modifier,
    currentAppLocale: AppLocale = requireCurrentAppLocale(),
    expiryDateStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    ),
    expiryDateMaxLines: Int = 1,
    expiryDateOverflow: TextOverflow = TextOverflow.Ellipsis,
    categoryStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.ExtraLight,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    ),
    categoryMaxLines: Int = 1
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = when {
                isLifetime -> stringResource(id = R.string.warranties_expiry_lifetime)
                else -> with(expiryDate!!) {
                    val monthName = stringArrayResource(
                        id = R.array.warranties_month_name
                    )[month.number - 1]

                    val dayWithSuffix = stringArrayResource(
                        id = R.array.warranties_day_with_suffix
                    )[day - 1]

                    stringResource(
                        id = R.string.warranties_expiry_date,
                        monthName,
                        dayWithSuffix,
                        year
                    )
                }
            },
            style = expiryDateStyle,
            maxLines = expiryDateMaxLines,
            overflow = expiryDateOverflow,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = category.title[currentAppLocale.languageTag].orEmpty(),
            style = categoryStyle,
            maxLines = categoryMaxLines
        )
    }
}