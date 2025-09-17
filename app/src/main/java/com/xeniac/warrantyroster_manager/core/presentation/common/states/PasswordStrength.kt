package com.xeniac.warrantyroster_manager.core.presentation.common.states

import androidx.compose.ui.graphics.Color
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Orange
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText

enum class PasswordStrength(
    val label: UiText? = null,
    val color: Color = Color.Transparent
) {
    SHORT(
        label = UiText.StringResource(
            R.string.core_password_strength_short,
            Constants.MIN_PASSWORD_LENGTH
        ),
        color = Red
    ),
    WEAK(
        label = UiText.StringResource(R.string.core_password_strength_weak),
        color = Red
    ),
    MEDIOCRE(
        label = UiText.StringResource(R.string.core_password_strength_mediocre),
        color = Orange
    ),
    STRONG(
        label = UiText.StringResource(R.string.core_password_strength_strong),
        color = Green
    )
}