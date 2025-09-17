package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.ContentLoadingAnimation
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.NetworkErrorMessage
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
                    loadingContent = { CategoriesListLoading() },
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

                        CategoriesList(
                            categories = categories,
                            onAction = onAction
                        )
                    }
                )
            }
        }
    }
}