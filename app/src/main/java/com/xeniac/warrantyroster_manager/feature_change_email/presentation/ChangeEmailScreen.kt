package com.xeniac.warrantyroster_manager.feature_change_email.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel

@Composable
fun ChangeEmailScreen(
    userViewModel: UserViewModel,
    onNavigateUp: () -> Unit,
    viewModel: ChangeEmailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    Text(
        text = "ChangeEmailScreen",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    )
}