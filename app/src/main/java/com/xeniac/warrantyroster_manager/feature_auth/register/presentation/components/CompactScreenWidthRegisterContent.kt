package com.xeniac.warrantyroster_manager.feature_auth.register.presentation.components

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
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.RegisterAction
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.states.RegisterState

@Composable
fun CompactScreenWidthRegisterContent(
    state: RegisterState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        top = 44.dp,
        bottom = 16.dp
    ),
    onAction: (action: RegisterAction) -> Unit,
    onNavigateUp: () -> Unit
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
        RegisterDescription(
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )

        Spacer(modifier = Modifier.height(44.dp))

        RegisterTextFields(
            isRegisterLoading = with(state) {
                isRegisterWithEmailLoading || isLoginWithGoogleLoading
                        || isLoginWithXLoading || isLoginWithGithubLoading
            },
            emailState = state.emailState,
            passwordState = state.passwordState,
            confirmPasswordState = state.confirmPasswordState,
            onAction = onAction,
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )

        PrivacyPolicyButton(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = horizontalPadding)
        )

        Spacer(modifier = Modifier.height(40.dp))

        BigButton(
            isLoading = state.isRegisterWithEmailLoading,
            text = stringResource(R.string.register_btn_register),
            onClick = {
                focusManager.clearFocus()
                onAction(RegisterAction.RegisterWithEmail)
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
            onLoginWithGoogleClick = { onAction(RegisterAction.LoginWithGoogle) },
            onLoginWithXClick = { onAction(RegisterAction.CheckPendingLoginWithX) },
            onLoginWithGithubClick = { onAction(RegisterAction.LoginWithGithub) },
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )

        Spacer(modifier = Modifier.height(44.dp))
        Spacer(modifier = Modifier.weight(1f))

        LoginButton(
            onClick = onNavigateUp,
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )
    }
}