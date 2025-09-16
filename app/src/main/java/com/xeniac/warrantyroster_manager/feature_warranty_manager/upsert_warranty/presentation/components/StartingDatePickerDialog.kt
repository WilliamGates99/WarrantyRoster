package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartingDatePickerDialog(
    modifier: Modifier = Modifier
) {
    var isDateVisible by remember { mutableStateOf(false) }

//    LaunchedEffect(Unit) {
//        delay(3000)
//        isDateVisible = true
//    }

    if (isDateVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = null,
            initialDisplayMode = DisplayMode.Picker
        )

        DatePickerDialog(
            onDismissRequest = { isDateVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis ->
//                            Timber.i(
//                                "selected date = ${
//                                    Instant.fromEpochMilliseconds(
//                                        epochMilliseconds = selectedDateMillis
//                                    )
//                                }"
//                            )
                        }
                        isDateVisible = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { isDateVisible = false }
                ) { Text("Cancel") }
            },
//            shape=,
//            colors=,
            modifier = Modifier
        ) {
            DatePicker(
                state = datePickerState,
//                title = ,
//                dateFormatter=,
//                headline=
                showModeToggle = true,
//                colors =
            )
        }
    }
}