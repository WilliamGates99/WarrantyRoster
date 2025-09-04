package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordAction
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.components.ReturnToLoginButton

@Composable
fun CompactScreenWidthResetPwInstructionContent(
    sentResetPasswordEmailsCount: Int,
    isTimerTicking: Boolean,
    isSendResetPasswordEmailLoading: Boolean,
    timerText: UiText,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        top = 44.dp,
        start = 24.dp,
        end = 24.dp,
        bottom = 16.dp
    ),
    onAction: (action: ForgotPasswordAction) -> Unit,
    onNavigateUp: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding)
            .padding(contentPadding)
    ) {
        ResetPwInstructionDescription()

        Spacer(modifier = Modifier.height(44.dp))

        EmailSentAnimation(
            sentResetPasswordEmailsCount = sentResetPasswordEmailsCount,
            modifier = Modifier.size(250.dp)
        )

        ResendEmailSection(
            sentResetPasswordEmailsCount = sentResetPasswordEmailsCount,
            isTimerTicking = isTimerTicking,
            isSendResetPasswordEmailLoading = isSendResetPasswordEmailLoading,
            timerText = timerText,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.weight(1f))

        ReturnToLoginButton(onClick = onNavigateUp)
    }
}