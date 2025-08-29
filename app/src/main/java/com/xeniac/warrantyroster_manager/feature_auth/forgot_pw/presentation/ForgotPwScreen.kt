package com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ForgotPwScreen(
    onNavigateUp: () -> Unit,
    viewModel: ForgotPwViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

}