@file:OptIn(ExperimentalTime::class)

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.di.entrypoints.requireDecimalFormat
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCheckbox
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomClickableOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.UpsertWarrantyAction
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import java.text.DecimalFormat
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun TitleAndDatesSection(
    isLoading: Boolean,
    titleState: CustomTextFieldState,
    isLifetimeWarranty: Boolean,
    selectedStartingDate: Instant?,
    selectedExpiryDate: Instant?,
    selectedStartingAndExpiryDatesError: UiText?,
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
            value = titleState.value,
            title = stringResource(id = R.string.upsert_warranty_textfield_title_title),
            placeholder = stringResource(id = R.string.upsert_warranty_textfield_title_hint),
            errorText = titleState.errorText,
            keyboardType = KeyboardType.Text,
            keyboardCapitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(UpsertWarrantyAction.TitleChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        )

        DatesSection(
            isLoading = isLoading,
            isLifetimeWarranty = isLifetimeWarranty,
            selectedStartingDate = selectedStartingDate,
            selectedExpiryDate = selectedExpiryDate,
            selectedStartingAndExpiryDatesError = selectedStartingAndExpiryDatesError,
            onAction = onAction
        )
    }
}

@Composable
private fun DatesSection(
    isLoading: Boolean,
    isLifetimeWarranty: Boolean,
    selectedStartingDate: Instant?,
    selectedExpiryDate: Instant?,
    selectedStartingAndExpiryDatesError: UiText?,
    modifier: Modifier = Modifier,
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        StartingAndExpiryDatesSection(
            isLoading = isLoading,
            isLifetimeWarranty = isLifetimeWarranty,
            selectedStartingDate = selectedStartingDate,
            selectedExpiryDate = selectedExpiryDate,
            selectedStartingAndExpiryDatesError = selectedStartingAndExpiryDatesError,
            onAction = onAction
        )

        CustomCheckbox(
            isLoading = isLoading,
            isChecked = isLifetimeWarranty,
            text = stringResource(id = R.string.upsert_warranty_checkbox_lifetime),
            onCheckedChange = { isChecked ->
                onAction(UpsertWarrantyAction.IsLifetimeWarrantyChanged(isChecked = isChecked))
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun StartingAndExpiryDatesSection(
    isLoading: Boolean,
    isLifetimeWarranty: Boolean,
    selectedStartingDate: Instant?,
    selectedExpiryDate: Instant?,
    selectedStartingAndExpiryDatesError: UiText?,
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StartingDateTextField(
                isLoading = isLoading,
                selectedStartingDate = selectedStartingDate,
                onAction = onAction,
                modifier = Modifier.weight(1f)
            )

            ExpiryDateTextField(
                isLoading = isLoading,
                isLifetimeWarranty = isLifetimeWarranty,
                selectedExpiryDate = selectedExpiryDate,
                onAction = onAction,
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(
            visible = selectedStartingAndExpiryDatesError != null,
            enter = errorEnterTransition,
            exit = errorExitTransition,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedStartingAndExpiryDatesError?.asString().orEmpty(),
                style = errorStyle,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun StartingDateTextField(
    isLoading: Boolean,
    selectedStartingDate: Instant?,
    modifier: Modifier = Modifier,
    decimalFormat: DecimalFormat = requireDecimalFormat(),
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    CustomClickableOutlinedTextField(
        isLoading = isLoading,
        value = selectedStartingDate?.toLocalDateTime(
            timeZone = timeZone
        )?.date?.let {
            stringResource(
                id = R.string.upsert_warranty_textfield_starting_date_value,
                decimalFormat.format(it.month.number),
                decimalFormat.format(it.day),
                it.year
            )
        }.orEmpty(),
        title = stringResource(id = R.string.upsert_warranty_textfield_starting_date_title),
        placeholder = stringResource(id = R.string.upsert_warranty_textfield_starting_date_hint),
        onTextFieldFocused = {
            onAction(UpsertWarrantyAction.ShowStartingDatePickerDialog)
        },
        modifier = modifier
    )
}

@Composable
private fun ExpiryDateTextField(
    isLoading: Boolean,
    isLifetimeWarranty: Boolean,
    selectedExpiryDate: Instant?,
    modifier: Modifier = Modifier,
    decimalFormat: DecimalFormat = requireDecimalFormat(),
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    onAction: (action: UpsertWarrantyAction) -> Unit
) {
    CustomClickableOutlinedTextField(
        isLoading = isLoading,
        enabled = !isLifetimeWarranty,
        value = selectedExpiryDate?.toLocalDateTime(
            timeZone = timeZone
        )?.date?.let {
            stringResource(
                id = R.string.upsert_warranty_textfield_expiry_date_value,
                decimalFormat.format(it.month.number),
                decimalFormat.format(it.day),
                it.year
            )
        }.orEmpty(),
        title = stringResource(id = R.string.upsert_warranty_textfield_expiry_date_title),
        placeholder = stringResource(id = R.string.upsert_warranty_textfield_expiry_date_hint),
        onTextFieldFocused = {
            onAction(UpsertWarrantyAction.ShowExpiryDatePickerDialog)
        },
        modifier = modifier
    )
}