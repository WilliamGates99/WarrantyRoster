package com.xeniac.warrantyroster_manager.feature_auth.common.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.LocaleBottomSheet
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupAuthNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Blue
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    rootNavController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val authNavController = rememberNavController()
    val backStackEntry by authNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

//    LaunchedEffect(key1 = currentDestination) {
//        val test2 = when {
//            currentDestination == null -> "null"
//            currentDestination.hasRoute(LoginScreen::class) -> "login"
//            currentDestination.hasRoute(RegisterScreen::class) -> "register"
//            else -> "else"
//        }
//
//        Timber.i("test2 = $test2")
//    }

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        topBar = {
            // TODO: HEADER (GET CURRENT DESTINATION)
//            OnboardingTopBar(
//                currentAppLocale = state.currentAppLocale,
//                pagerState = pagerState,
//                onAction = viewModel::onAction
//            )
        },
        containerColor = Blue,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // TODO: CLIP SHAPE AND BACKGROUND
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SetupAuthNavGraph(
                rootNavController = rootNavController,
                authNavController = authNavController
            )
        }
    }

    LocaleBottomSheet(
        isVisible = state.isLocaleBottomSheetVisible,
        currentAppLocale = state.currentAppLocale ?: AppLocale.DEFAULT,
        onDismiss = { viewModel.onAction(AuthAction.DismissLocaleBottomSheet) },
        onLocaleItemClick = { newAppLocale ->
            viewModel.onAction(AuthAction.SetCurrentAppLocale(newAppLocale = newAppLocale))
        }
    )
}