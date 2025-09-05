package com.xeniac.warrantyroster_manager.core.presentation.base_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.imageLoader
import com.xeniac.warrantyroster_manager.core.presentation.base_screen.components.PostNotificationPermissionHandler
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupBaseNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.getNavigationBarHeightDp
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent

@Composable
fun BaseScreen(
    onLogoutNavigate: () -> Unit,
    viewModel: BaseViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val baseNavController = rememberNavController()
    val backStackEntry by baseNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    var isBottomAppBarVisible by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = currentDestination) {
//        isBottomAppBarVisible = NavigationBarItems.entries.find { navItem ->
//            currentDestination?.hierarchy?.any {
//                navItem.destinationScreen?.let { destinationScreen ->
//                    it.hasRoute(route = destinationScreen::class)
//                } ?: false
//            } ?: false
//        } != null
    }

    ObserverAsEvent(userViewModel.logoutEventChannel) { event ->
        when (event) {
            UiEvent.ClearCoilCache -> {
                context.imageLoader.diskCache?.clear()
                context.imageLoader.memoryCache?.clear()
            }
            UiEvent.NavigateToAuthScreen -> onLogoutNavigate()
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SwipeableSnackbar(hostState = snackbarHostState) },
        bottomBar = {
//            CustomNavigationBar(
//                isBottomAppBarVisible = isBottomAppBarVisible,
//                currentDestination = currentDestination,
//                onItemClick = { screen ->
//                    when (screen) {
//                        // If the destination screen is the nav graph start destination, pop up to it
//                        HomeScreen -> baseNavController.popBackStack(
//                            route = screen,
//                            inclusive = false
//                        )
//                        else -> baseNavController.navigate(screen) {
//                            // Avoid multiple copies of the same destination when re-selecting the same item
//                            launchSingleTop = true
//
//                            /*
//                            Pop up to the start destination of the graph to
//                            avoid building up a large stack of destinations
//                            on the back stack as user selects items
//                            */
//                            popUpTo(id = baseNavController.graph.startDestinationId)
//                        }
//                    }
//                },
//                onAddWarrantyFabClick = {
//                    baseNavController.navigate(AddWarrantyScreen) {
//                        launchSingleTop = true
//                        popUpTo(id = baseNavController.graph.startDestinationId)
//                    }
//                }
//            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SetupBaseNavGraph(
            baseNavController = baseNavController,
            // TODO: CHANGE FAB SIZE
            bottomPadding = innerPadding.calculateBottomPadding() - getNavigationBarHeightDp() + 28.dp, // Half of FAB Height = 28.dp
            userViewModel = userViewModel
        )
    }

    PostNotificationPermissionHandler(
        isPermissionDialogVisible = true,
        permissionDialogQueue = listOf(""),
        onAction = viewModel::onAction
    )
}