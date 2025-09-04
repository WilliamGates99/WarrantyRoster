package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.common.ForgotPasswordViewModel

@Composable
fun ResetPwInstructionScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Text(
        text = "reset pw instruction screen",
        color = Blue,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .clickable { onNavigateUp() }
    )
}