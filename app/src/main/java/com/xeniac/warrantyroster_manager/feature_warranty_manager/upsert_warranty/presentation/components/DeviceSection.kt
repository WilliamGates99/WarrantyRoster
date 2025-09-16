package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.UpsertWarrantyAction

@Composable
fun DeviceSection(
    isLoading: Boolean,
    brandState: CustomTextFieldState,
    modelState: CustomTextFieldState,
    serialNumberState: CustomTextFieldState,
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
        CustomOutlinedTextField(
            isLoading = isLoading,
            value = brandState.value,
            title = stringResource(id = R.string.upsert_warranty_textfield_brand_title),
            placeholder = stringResource(id = R.string.upsert_warranty_textfield_brand_hint),
            supportingText = stringResource(id = R.string.core_textfield_helper_optional),
            errorText = brandState.errorText,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(UpsertWarrantyAction.BrandChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isLoading,
            value = modelState.value,
            title = stringResource(id = R.string.upsert_warranty_textfield_model_title),
            placeholder = stringResource(id = R.string.upsert_warranty_textfield_model_hint),
            supportingText = stringResource(id = R.string.core_textfield_helper_optional),
            errorText = modelState.errorText,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next,
            onValueChange = { newValue ->
                onAction(UpsertWarrantyAction.ModelChanged(newValue))
            },
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            isLoading = isLoading,
            value = serialNumberState.value,
            title = stringResource(id = R.string.upsert_warranty_textfield_serial_number_title),
            placeholder = stringResource(id = R.string.upsert_warranty_textfield_serial_number_hint),
            supportingText = stringResource(id = R.string.core_textfield_helper_optional),
            errorText = serialNumberState.errorText,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Characters,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(UpsertWarrantyAction.SerialNumberChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}