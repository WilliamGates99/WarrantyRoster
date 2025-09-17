package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireCurrentAppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomClickableOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.shimmerEffect
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGray50
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toDp
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.UpsertWarrantyAction

@Composable
fun CategoryAndDescriptionSection(
    isLoading: Boolean,
    selectedCategory: WarrantyCategory?,
    selectedCategoryError: UiText?,
    descriptionState: CustomTextFieldState,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 16.dp
    ),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
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
            .padding(contentPadding)
    ) {
        CategorySelectorSection(
            isLoading = isLoading,
            selectedCategory = selectedCategory,
            selectedCategoryError = selectedCategoryError,
            onAction = onAction
        )

        CustomOutlinedTextField(
            isLoading = isLoading,
            value = descriptionState.value,
            title = stringResource(id = R.string.upsert_warranty_textfield_description_title),
            placeholder = stringResource(id = R.string.upsert_warranty_textfield_description_hint),
            supportingText = stringResource(id = R.string.core_textfield_helper_optional),
            errorText = descriptionState.errorText,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(UpsertWarrantyAction.DescriptionChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CategorySelectorSection(
    isLoading: Boolean,
    selectedCategory: WarrantyCategory?,
    selectedCategoryError: UiText?,
    modifier: Modifier = Modifier,
    errorEnterTransition: EnterTransition = fadeIn() + expandVertically(),
    errorExitTransition: ExitTransition = shrinkVertically() + fadeOut(),
    errorStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        color = Red
    ),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        CategorySelector(
            isLoading = isLoading,
            selectedCategory = selectedCategory,
            onAction = onAction
        )

        AnimatedVisibility(
            visible = selectedCategoryError != null,
            enter = errorEnterTransition,
            exit = errorExitTransition,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedCategoryError?.asString().orEmpty(),
                style = errorStyle,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun CategorySelector(
    isLoading: Boolean,
    selectedCategory: WarrantyCategory?,
    modifier: Modifier = Modifier,
    currentAppLocale: AppLocale = requireCurrentAppLocale(),
    categoryTitle: String? = selectedCategory?.title?.get(currentAppLocale.languageTag),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    var textfieldHeightPx by remember { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
    ) {
        CustomClickableOutlinedTextField(
            isLoading = isLoading,
            value = categoryTitle,
            title = stringResource(id = R.string.upsert_warranty_textfield_category_title),
            placeholder = stringResource(id = R.string.upsert_warranty_textfield_category_hint),
            onTextFieldFocused = {
                onAction(UpsertWarrantyAction.ShowCategoriesBottomSheet)
            },
            onTextFieldSizeChanged = {
                textfieldHeightPx = it.height
            },
            modifier = Modifier.weight(1f)
        )

        CategoryIcon(
            iconUrl = selectedCategory?.iconUrl,
            contentDescription = categoryTitle,
            size = textfieldHeightPx.toDp()
        )
    }
}

@Composable
private fun CategoryIcon(
    iconUrl: String?,
    contentDescription: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    background: Color = MaterialTheme.colorScheme.dynamicGray50,
    contentPadding: PaddingValues = PaddingValues(all = 12.dp),
    placeholder: Painter = painterResource(id = R.drawable.ic_core_warranty_category_placeholder),
    placeholderColor: Color = Red
) {
    SubcomposeAsyncImage(
        model = iconUrl ?: BuildConfig.DEFAULT_CATEGORY_ICON_URL,
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
                    radius = 8.dp,
                    color = Black,
                    alpha = 0.16f
                )
            )
            .clip(shape)
            .background(background)
    )
}