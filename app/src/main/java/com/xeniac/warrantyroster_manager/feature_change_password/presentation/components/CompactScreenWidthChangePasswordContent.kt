package com.xeniac.warrantyroster_manager.feature_change_password.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.BigButton
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.ChangePasswordAction
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.states.ChangePasswordState

@Composable
fun CompactScreenWidthChangePasswordContent(
    state: ChangePasswordState,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onAction: (action: ChangePasswordAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets(top = innerPadding.calculateTopPadding()))
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets(bottom = innerPadding.calculateBottomPadding()))
            .safeDrawingPadding()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 8.dp,
                bottom = 24.dp
            )
    ) {
        ChangePasswordAnimation(modifier = Modifier.size(250.dp))

        Spacer(modifier = Modifier.height(8.dp))

        ChangePasswordTextFields(
            isChangeUserPasswordLoading = state.isChangeUserPasswordLoading,
            currentPasswordState = state.currentPasswordState,
            newPasswordState = state.newPasswordState,
            confirmNewPasswordState = state.confirmNewPasswordState,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(40.dp))

        BigButton(
            isLoading = state.isChangeUserPasswordLoading,
            text = stringResource(R.string.change_password_btn_change),
            onClick = {
                focusManager.clearFocus()
                onAction(ChangePasswordAction.ChangeUserPassword)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}