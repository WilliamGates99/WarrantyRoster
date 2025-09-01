package com.xeniac.warrantyroster_manager.feature_auth.login.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.isWindowWidthSizeCompact
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.components.CompactScreenWidthLoginContent

@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val test = isSystemInDarkTheme()
    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (isWindowWidthSizeCompact()) {
            true -> CompactScreenWidthLoginContent(
//                onNavigateToAuthScreen = onNavigateToAuthScreen,
                bottomPadding = innerPadding.calculateBottomPadding()
            )
            false -> {
//                MediumScreenWidthLoginContent(
//                    innerPadding = innerPadding,
//                    onNavigateToAuthScreen = onNavigateToAuthScreen,
//                    bottomPadding = innerPadding.calculateBottomPadding()
//                )
            }
        }
    }
}