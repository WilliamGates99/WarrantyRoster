package com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.BigButton
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components.OtherLoginMethodsDividerMediumWidth
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components.OtherLoginMethodsMediumWidth
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginAction
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.states.LoginState

@Composable
fun MediumScreenWidthLoginContent(
    state: LoginState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    onAction: (action: LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToForgotPwScreen: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxSize()
    ) {
        LoginWithEmailSection(
            state = state,
            bottomPadding = bottomPadding,
            onAction = onAction,
            onNavigateToRegisterScreen = onNavigateToRegisterScreen,
            onNavigateToForgotPwScreen = onNavigateToForgotPwScreen,
            modifier = modifier
                .align(Alignment.Top)
                .weight(1f)
        )

        OtherLoginMethodsDividerMediumWidth()

        OtherLoginMethodsMediumWidth(
            isLoginWithGoogleLoading = state.isLoginWithGoogleLoading,
            isLoginWithXLoading = state.isLoginWithXLoading,
            isLoginWithGithubLoading = state.isLoginWithGithubLoading,
            onLoginWithGoogleClick = { onAction(LoginAction.LoginWithGoogle) },
            onLoginWithXClick = { onAction(LoginAction.CheckPendingLoginWithX) },
            onLoginWithGithubClick = { onAction(LoginAction.LoginWithGithub) }
        )
    }
}

@Composable
private fun LoginWithEmailSection(
    state: LoginState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        top = 32.dp,
        bottom = 16.dp
    ),
    onAction: (action: LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToForgotPwScreen: () -> Unit
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
        LoginDescription()

        Spacer(modifier = Modifier.height(32.dp))

        LoginTextFields(
            isLoginLoading = with(state) {
                isLoginWithEmailLoading || isLoginWithGoogleLoading
                        || isLoginWithXLoading || isLoginWithGithubLoading
            },
            emailState = state.emailState,
            passwordState = state.passwordState,
            onAction = onAction
        )

        ForgotPwButton(
            onClick = onNavigateToForgotPwScreen,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(28.dp))

        BigButton(
            isLoading = state.isLoginWithEmailLoading,
            text = stringResource(R.string.login_btn_login),
            onClick = {
                focusManager.clearFocus()
                onAction(LoginAction.LoginWithEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(44.dp))
        Spacer(modifier = Modifier.weight(1f))

        RegisterButton(onClick = onNavigateToRegisterScreen)
    }
}