package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireCurrentAppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.ContentLoadingAnimation
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.NetworkErrorMessage
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.shimmerEffect
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.UpsertWarrantyAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesBottomSheet(
    isVisible: Boolean,
    isCategoriesLoading: Boolean,
    categories: List<WarrantyCategory>?,
    errorMessage: UiText?,
    selectedCategory: WarrantyCategory?,
    modifier: Modifier = Modifier,
    sheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = true,
        securePolicy = SecureFlagPolicy.Inherit
    ),
    headline: String = stringResource(id = R.string.upsert_warranty_textfield_category_hint).uppercase(),
    headlineStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    ),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    if (isVisible) {
        val focusManager = LocalFocusManager.current
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

        ModalBottomSheet(
            sheetState = sheetState,
            properties = sheetProperties,
            onDismissRequest = {
                focusManager.clearFocus()
                onAction(UpsertWarrantyAction.DismissCategoriesBottomSheet)
            },
            modifier = modifier
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = headline,
                    style = headlineStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                )

                ContentLoadingAnimation(
                    isContentLoading = isCategoriesLoading,
                    loadingContent = { LoadingCategories() },
                    content = {
                        errorMessage?.let {
                            NetworkErrorMessage(
                                message = it,
                                onRetryClick = { onAction(UpsertWarrantyAction.GetCategories) }
                            )
                            return@ContentLoadingAnimation
                        }

                        if (categories.isNullOrEmpty()) {
                            NetworkErrorMessage(
                                message = stringResource(id = R.string.error_something_went_wrong),
                                onRetryClick = { onAction(UpsertWarrantyAction.GetCategories) }
                            )
                            return@ContentLoadingAnimation
                        }

                        SelectableCategories(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onAction = onAction
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingCategories(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp)
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
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 2.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .shimmerEffect()
    )
}

@Composable
private fun SelectableCategories(
    categories: List<WarrantyCategory>,
    selectedCategory: WarrantyCategory?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
    ) {
        items(
            items = categories,
            key = { it.id }
        ) { category ->
            CategoryItem(
                isSelected = selectedCategory == category,
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
    isSelected: Boolean,
    category: WarrantyCategory,
    modifier: Modifier = Modifier,
    currentAppLocale: AppLocale = requireCurrentAppLocale(),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 18.dp,
        vertical = 12.dp
    ),
    titleStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onSurface
    ),
    title: String? = category.title[currentAppLocale.languageTag],
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Start
        ),
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(contentPadding)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )

        Text(
            text = title.orEmpty(),
            style = titleStyle,
            modifier = Modifier.weight(1f)
        )
    }
}