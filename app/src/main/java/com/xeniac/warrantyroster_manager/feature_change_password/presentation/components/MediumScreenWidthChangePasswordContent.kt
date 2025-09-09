package com.xeniac.warrantyroster_manager.feature_change_password.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
fun MediumScreenWidthChangePasswordContent(
    state: ChangePasswordState,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onAction: (action: ChangePasswordAction) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 44.dp),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
            )
            .safeDrawingPadding()
            .padding(start = 44.dp)
    ) {
        ChangePasswordAnimation(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.35f)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        )

        ScrollableContent(
            state = state,
            onAction = onAction,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun ScrollableContent(
    state: ChangePasswordState,
    modifier: Modifier = Modifier,
    onAction: (action: ChangePasswordAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                end = 44.dp,
                top = 24.dp,
                bottom = 24.dp
            )
    ) {
        ChangePasswordTextFields(
            isChangeUserPasswordLoading = state.isChangeUserPasswordLoading,
            currentPasswordState = state.currentPasswordState,
            newPasswordState = state.newPasswordState,
            confirmNewPasswordState = state.confirmNewPasswordState,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(28.dp))

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