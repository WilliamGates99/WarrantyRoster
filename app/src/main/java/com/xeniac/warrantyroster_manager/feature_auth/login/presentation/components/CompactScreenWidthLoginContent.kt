package com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.BigButton
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components.OtherLoginMethodsCompactWidth
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components.OtherLoginMethodsDividerCompactWidth
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginAction
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.states.LoginState

@Composable
fun CompactScreenWidthLoginContent(
    state: LoginState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        top = 44.dp,
        bottom = 12.dp
    ),
    onAction: (action: LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToForgotPwScreen: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val horizontalPadding by remember { derivedStateOf { 24.dp } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding)
            .padding(contentPadding)
            .imePadding()
    ) {
        LoginDescription(
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )

        Spacer(modifier = Modifier.height(44.dp))

        LoginTextFields(
            isLoginLoading = with(state) {
                isLoginWithEmailLoading || isLoginWithGoogleLoading
                        || isLoginWithXLoading || isLoginWithGithubLoading
            },
            emailState = state.emailState,
            passwordState = state.passwordState,
            onAction = onAction,
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )

        ForgotPwButton(
            onClick = onNavigateToForgotPwScreen,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = horizontalPadding)
        )

        Spacer(modifier = Modifier.height(40.dp))

        BigButton(
            isLoading = state.isLoginWithEmailLoading,
            text = stringResource(R.string.login_btn_login),
            onClick = {
                focusManager.clearFocus()
                onAction(LoginAction.LoginWithEmail)
            },
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .fillMaxWidth()
        )

        OtherLoginMethodsDividerCompactWidth()

        OtherLoginMethodsCompactWidth(
            isLoginWithGoogleLoading = state.isLoginWithGoogleLoading,
            isLoginWithXLoading = state.isLoginWithXLoading,
            isLoginWithGithubLoading = state.isLoginWithGithubLoading,
            onLoginWithGoogleClick = { onAction(LoginAction.LoginWithGoogle) },
            onLoginWithXClick = { onAction(LoginAction.CheckPendingLoginWithX) },
            onLoginWithGithubClick = { onAction(LoginAction.CheckPendingLoginWithGithub) },
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )

        Spacer(modifier = Modifier.height(44.dp))
        Spacer(modifier = Modifier.weight(1f))

        RegisterButton(
            onClick = onNavigateToRegisterScreen,
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )
    }
}