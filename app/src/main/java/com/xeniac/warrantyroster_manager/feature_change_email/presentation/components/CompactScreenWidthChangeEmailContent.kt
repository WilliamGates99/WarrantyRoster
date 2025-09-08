package com.xeniac.warrantyroster_manager.feature_change_email.presentation.components

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
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.ChangeEmailAction
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.states.ChangeEmailState

@Composable
fun CompactScreenWidthChangeEmailContent(
    state: ChangeEmailState,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onAction: (action: ChangeEmailAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
            )
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 8.dp,
                bottom = 24.dp
            )
    ) {
        ChangeEmailAnimation(modifier = Modifier.size(250.dp))

        Spacer(modifier = Modifier.height(8.dp))

        ChangeEmailTextFields(
            isSubmitLoading = state.isSubmitLoading,
            passwordState = state.passwordState,
            newEmailState = state.newEmailState,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(40.dp))

        BigButton(
            isLoading = state.isSubmitLoading,
            text = stringResource(R.string.change_email_btn_change),
            onClick = {
                focusManager.clearFocus()
                onAction(ChangeEmailAction.SubmitNewEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}