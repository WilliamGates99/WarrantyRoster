package com.xeniac.warrantyroster_manager.core.presentation.common.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    colors: DatePickerColors = DatePickerDefaults.colors().copy(
        containerColor = MaterialTheme.colorScheme.surface,
        dateTextFieldColors = outlinedTextFieldColors()
    ),
    initialSelectedDateMillis: Long? = null,
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    showModeToggle: Boolean = true,
    dateFormatter: DatePickerFormatter = remember { DatePickerDefaults.dateFormatter() },
    title: String? = null,
    confirmButton: String = stringResource(id = R.string.core_date_picker_btn_confirm),
    cancelButton: String = stringResource(id = R.string.core_date_picker_btn_cancel),
    onDismissRequest: () -> Unit,
    onConfirmClick: (selectedDateMillis: Long?) -> Unit,
    onCancelClick: (() -> Unit)? = null
) {
    if (isVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayMode = initialDisplayMode
        )

        DatePickerDialog(
            shape = shape,
            colors = colors,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                DatePickerConfirmButton(
                    text = confirmButton,
                    onClick = {
                        onConfirmClick(datePickerState.selectedDateMillis)
                        onDismissRequest()
                    }
                )
            },
            dismissButton = {
                DatePickerCancelButton(
                    text = cancelButton,
                    onClick = onCancelClick ?: onDismissRequest
                )
            },
            modifier = modifier
        ) {
            DatePicker(
                state = datePickerState,
                colors = colors,
                showModeToggle = showModeToggle,
                dateFormatter = dateFormatter,
                title = title?.let {
                    { DatePickerTitle(title = it) }
                }
            )
        }
    }
}

@Composable
private fun DatePickerTitle(
    title: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        end = 12.dp,
        top = 16.dp
    )
) {
    Text(
        text = title,
        modifier = modifier.padding(contentPadding)
    )
}

@Composable
private fun DatePickerConfirmButton(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(4.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
    ),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        contentPadding = contentPadding,
        colors = colors,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
private fun DatePickerCancelButton(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(4.dp),
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        containerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
    ),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        shape = shape,
        contentPadding = contentPadding,
        colors = colors,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}