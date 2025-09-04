package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.forgot_pw.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.BigButton
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomOutlinedTextField
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordAction
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.components.ReturnToLoginButton
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.states.ForgotPasswordState

@Composable
fun MediumScreenWidthForgotPwContent(
    state: ForgotPasswordState,
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
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding)
            .padding(contentPadding)
            .imePadding()
    ) {
        ForgotPwDescription()

        Spacer(modifier = Modifier.height(32.dp))

        CustomOutlinedTextField(
            isLoading = state.isSendResetPasswordEmailLoading,
            value = state.emailState.value,
            title = stringResource(id = R.string.forgot_pw_textfield_email_title),
            placeholder = stringResource(id = R.string.forgot_pw_textfield_email_hint),
            errorText = state.emailState.errorText,
            contentType = ContentType.Username,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onValueChange = { newValue ->
                onAction(ForgotPasswordAction.EmailChanged(newValue))
            },
            keyboardAction = {
                focusManager.clearFocus()
                onAction(ForgotPasswordAction.SendResetPasswordEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        BigButton(
            isLoading = state.isSendResetPasswordEmailLoading,
            text = stringResource(R.string.forgot_pw_btn_send_email),
            onClick = {
                focusManager.clearFocus()
                onAction(ForgotPasswordAction.SendResetPasswordEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(44.dp))
        Spacer(modifier = Modifier.weight(1f))

        ReturnToLoginButton(onClick = onNavigateUp)
    }
}