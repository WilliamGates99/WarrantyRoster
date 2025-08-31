package com.xeniac.warrantyroster_manager.core.presentation.common.states

import androidx.compose.ui.graphics.Color
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red

enum class ConfirmPasswordMatchingState(
    val color: Color = Color.Transparent
) {
    BLANK_CONFIRM_PASSWORD,
    NOT_MATCHING(color = Red),
    MATCHING(color = Green)
}