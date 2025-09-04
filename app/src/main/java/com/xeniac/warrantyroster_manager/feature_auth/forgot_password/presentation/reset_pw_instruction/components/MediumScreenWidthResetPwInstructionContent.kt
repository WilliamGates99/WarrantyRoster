package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.core.presentation.common.states.CustomTextFieldState
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordAction
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.states.ForgotPasswordState

@Composable
fun MediumScreenWidthResetPwInstructionContent(
    state: ForgotPasswordState,
    timerText: UiText,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        end = 24.dp,
        top = 32.dp,
        bottom = 12.dp
    ),
    onAction: (action: ForgotPasswordAction) -> Unit,
    onNavigateUp: () -> Unit
) {

}