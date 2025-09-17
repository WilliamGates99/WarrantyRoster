package com.xeniac.warrantyroster_manager.core.presentation.common.states

import android.os.Parcelable
import androidx.compose.ui.text.input.TextFieldValue
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<TextFieldValue, TextFieldValueParceler>
data class ConfirmPasswordTextFieldState(
    val value: TextFieldValue = TextFieldValue(),
    val passwordMatchingState: ConfirmPasswordMatchingState? = null,
    val errorText: UiText? = null
) : Parcelable