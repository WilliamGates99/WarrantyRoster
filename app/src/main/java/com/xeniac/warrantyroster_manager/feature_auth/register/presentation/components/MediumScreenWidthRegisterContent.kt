package com.xeniac.warrantyroster_manager.feature_auth.register.presentation.components

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
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.RegisterAction
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.states.RegisterState

@Composable
fun MediumScreenWidthRegisterContent(
    state: RegisterState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    onAction: (action: RegisterAction) -> Unit,
    onNavigateUp: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxSize()
    ) {
        RegisterWithEmailSection(
            state = state,
            bottomPadding = bottomPadding,
            onAction = onAction,
            onNavigateUp = onNavigateUp,
            modifier = modifier
                .align(Alignment.Top)
                .weight(1f)
        )

        OtherLoginMethodsDividerMediumWidth()

        OtherLoginMethodsMediumWidth(
            isLoginWithGoogleLoading = state.isLoginWithGoogleLoading,
            isLoginWithXLoading = state.isLoginWithXLoading,
            isLoginWithGithubLoading = state.isLoginWithGithubLoading,
            onLoginWithGoogleClick = { onAction(RegisterAction.LoginWithGoogle) },
            onLoginWithXClick = { onAction(RegisterAction.CheckPendingLoginWithX) },
            onLoginWithGithubClick = { onAction(RegisterAction.CheckPendingLoginWithGithub) }
        )
    }
}

@Composable
private fun RegisterWithEmailSection(
    state: RegisterState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        top = 32.dp,
        bottom = 12.dp
    ),
    onAction: (action: RegisterAction) -> Unit,
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
        RegisterDescription()

        Spacer(modifier = Modifier.height(32.dp))

        RegisterTextFields(
            isRegisterLoading = with(state) {
                isRegisterWithEmailLoading || isLoginWithGoogleLoading
                        || isLoginWithXLoading || isLoginWithGithubLoading
            },
            emailState = state.emailState,
            passwordState = state.passwordState,
            confirmPasswordState = state.confirmPasswordState,
            onAction = onAction
        )

        PrivacyPolicyButton(modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(28.dp))

        BigButton(
            isLoading = state.isRegisterWithEmailLoading,
            text = stringResource(R.string.register_btn_register),
            onClick = {
                focusManager.clearFocus()
                onAction(RegisterAction.RegisterWithEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(44.dp))
        Spacer(modifier = Modifier.weight(1f))

        LoginButton(onClick = onNavigateUp)
    }
}