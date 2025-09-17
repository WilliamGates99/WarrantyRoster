package com.xeniac.warrantyroster_manager.feature_base.presentation

import android.app.Activity.RESULT_OK
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.imageLoader
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserAction
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showActionSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showLongSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupBaseNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.UpsertWarrantyScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.getNavigationBarHeightDp
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.feature_base.presentation.components.AppReviewDialog
import com.xeniac.warrantyroster_manager.feature_base.presentation.components.AppUpdateBottomSheet
import com.xeniac.warrantyroster_manager.feature_base.presentation.components.CustomNavigationBar
import com.xeniac.warrantyroster_manager.feature_base.presentation.components.NavigationBarItems
import com.xeniac.warrantyroster_manager.feature_base.presentation.components.PostNotificationPermissionHandler
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    onLogoutNavigate: () -> Unit,
    viewModel: BaseViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val baseNavController = rememberNavController()
    val backStackEntry by baseNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    var isBottomAppBarVisible by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = currentDestination) {
        isBottomAppBarVisible = NavigationBarItems.entries.find { navItem ->
            currentDestination?.hierarchy?.any {
                it.hasRoute(route = navItem.destinationScreen::class)
            } ?: false
        } != null
    }

    val appUpdateResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.e("Something went wrong with the in-app update flow.")
            }
        }
    )

    ObserverAsEvent(flow = viewModel.inAppUpdatesEventChannel) { event ->
        when (event) {
            is BaseUiEvent.StartAppUpdateFlow -> {
                viewModel.appUpdateManager.get().startUpdateFlowForResult(
                    event.appUpdateInfo,
                    appUpdateResultLauncher,
                    viewModel.appUpdateOptions.get()
                )
            }
            BaseUiEvent.ShowCompleteAppUpdateSnackbar -> context.showActionSnackbar(
                message = UiText.StringResource(R.string.base_app_update_message),
                actionLabel = UiText.StringResource(R.string.base_app_update_action),
                scope = scope,
                snackbarHostState = snackbarHostState,
                onAction = { viewModel.appUpdateManager.get().completeUpdate() }
            )
            BaseUiEvent.CompleteFlexibleAppUpdate -> {
                viewModel.appUpdateManager.get().completeUpdate()
            }
        }
    }

    ObserverAsEvent(flow = viewModel.inAppReviewEventChannel) { event ->
        when (event) {
            BaseUiEvent.LaunchInAppReview -> {
                state.inAppReviewInfo?.let { reviewInfo ->
                    viewModel.reviewManager.get().launchReviewFlow(
                        activity,
                        reviewInfo
                    ).addOnCompleteListener {
                        /*
                        The flow has finished.
                        The API does not indicate whether the user reviewed or not,
                        or even whether the review dialog was shown.
                        Thus, no matter the result, we continue our app flow.
                         */
                        if (it.isSuccessful) {
                            Timber.i("App review flow was completed successfully.")
                        } else {
                            Timber.e("Something went wrong with showing the app review flow:")
                            it.exception?.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    ObserverAsEvent(userViewModel.getUserProfileEventChannel) { event ->
        when (event) {
            UiEvent.ForceLogoutUnauthorizedUser -> {
                userViewModel.onAction(UserAction.Logout)
            }
            is UiEvent.ShowLongSnackbar -> context.showLongSnackbar(
                message = event.message,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            else -> Unit
        }
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
            CustomNavigationBar(
                isBottomAppBarVisible = isBottomAppBarVisible,
                currentDestination = currentDestination,
                onItemClick = { screen ->
                    baseNavController.navigate(route = screen) {
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true

                        /*
                        Pop up to the start destination of the graph to
                        avoid building up a large stack of destinations
                        on the back stack as user selects items
                        */
                        popUpTo(id = baseNavController.graph.startDestinationId)
                    }
                },
                onAddWarrantyFabClick = {
                    baseNavController.navigate(route = UpsertWarrantyScreen()) {
                        launchSingleTop = true
                        popUpTo(id = baseNavController.graph.startDestinationId)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SetupBaseNavGraph(
            baseNavController = baseNavController,
            bottomPadding = innerPadding.calculateBottomPadding() - getNavigationBarHeightDp() + 28.dp, // Half of FAB Height = 28.dp
            userViewModel = userViewModel
        )
    }

    PostNotificationPermissionHandler(
        isPermissionDialogVisible = true,
        permissionDialogQueue = listOf(""),
        onAction = viewModel::onAction
    )

    AppUpdateBottomSheet(
        isVisible = state.latestAppUpdateInfo != null,
        onAction = viewModel::onAction
    )

    AppReviewDialog(
        isVisible = state.isAppReviewDialogVisible,
        onAction = viewModel::onAction
    )
}