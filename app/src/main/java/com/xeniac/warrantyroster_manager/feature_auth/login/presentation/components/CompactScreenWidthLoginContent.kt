package com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginAction
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.states.LoginState

@Composable
fun CompactScreenWidthLoginContent(
    state: LoginState,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        start = 24.dp,
        end = 24.dp,
        top = 44.dp,
        bottom = 24.dp
    ),
    onAction: (action: LoginAction) -> Unit,
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToForgotPwScreen: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding)
            .padding(contentPadding)
    ) {
        LoginDescription(
            modifier = Modifier.padding(bottom = 44.dp)
        )

//        Spacer(modifier = Modifier.height(44.dp))

        LoginTextFields(
            isLoginLoading = with(state) {
                isLoginWithEmailLoading || isLoginWithGoogleLoading
                        || isLoginWithXLoading || isLoginWithFacebookLoading
            },
            emailState = state.emailState,
            passwordState = state.passwordState,
            onAction = onAction
        )

        // TODO: FORGOT PASSWORD

        // TODO: LOGIN BTN

        // TODO: OTHER METHODS DIVIDER

        // TODO: OTHER METHODS

        // TODO: REGISTER BTN
    }
}