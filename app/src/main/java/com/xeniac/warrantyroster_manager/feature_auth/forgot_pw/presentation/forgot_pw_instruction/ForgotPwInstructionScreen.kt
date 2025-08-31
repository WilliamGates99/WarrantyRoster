package com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation.forgot_pw_instruction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ForgotPwInstructionScreen(
    onNavigateUp: () -> Unit,
    viewModel: ForgotPwInstructionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

}