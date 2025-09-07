package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Green
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordAction

@Composable
fun ResendEmailSection(
    sentResetPasswordEmailsCount: Int,
    isTimerTicking: Boolean,
    isSendResetPasswordEmailLoading: Boolean,
    timerText: UiText,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn(),
    exitTransition: ExitTransition = fadeOut(),
    onAction: (action: ForgotPasswordAction) -> Unit
) {
    AnimatedContent(
        targetState = isTimerTicking,
        transitionSpec = { enterTransition.togetherWith(exit = exitTransition) },
        modifier = modifier.fillMaxWidth()
    ) { isTicking ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            when {
                isTicking -> {
                    EmailSentText(sentResetPasswordEmailsCount = sentResetPasswordEmailsCount)
                    TimerText(timerText = timerText)
                }
                else -> {
                    ResendEmailTitle()
                    ResendEmailButton(
                        isLoading = isSendResetPasswordEmailLoading,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailSentText(
    sentResetPasswordEmailsCount: Int,
    modifier: Modifier = Modifier,
    text: String = stringResource(
        id = when {
            sentResetPasswordEmailsCount > 1 -> R.string.reset_pw_instruction_email_sent
            else -> R.string.reset_pw_instruction_email_sent_first_time
        }
    ),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicBlack
    )
) {
    Text(
        text = text,
        style = textStyle,
        modifier = modifier
    )
}

@Composable
private fun TimerText(
    timerText: UiText,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 14.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        color = Green
    )
) {
    Text(
        text = timerText.asString(),
        style = textStyle,
        modifier = modifier.animateContentSize()
    )
}

@Composable
private fun ResendEmailTitle(
    modifier: Modifier = Modifier,
    text: String = stringResource(id = R.string.reset_pw_instruction_resend_title),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicGrayDark
    )
) {
    Text(
        text = text,
        style = textStyle,
        modifier = modifier
    )
}

@Composable
private fun ResendEmailButton(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    contentPadding: PaddingValues = PaddingValues(all = 4.dp),
    text: String = stringResource(id = R.string.reset_pw_instruction_btn_resend),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.dynamicBlack.copy(alpha = if (isLoading) 0.60f else 1f)
    ),
    onAction: (action: ForgotPasswordAction) -> Unit
) {
    Text(
        text = text,
        style = textStyle,
        modifier = modifier
            .clip(shape)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClick = { onAction(ForgotPasswordAction.SendResetPasswordEmail) },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = textStyle.color)
            )
            .padding(contentPadding)
    )
}