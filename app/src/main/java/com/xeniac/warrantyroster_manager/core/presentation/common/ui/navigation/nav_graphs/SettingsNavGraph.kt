package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ChangeEmailScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ChangePasswordScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LinkedAccountsScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.SettingsScreen
import com.xeniac.warrantyroster_manager.feature_change_email.presentation.ChangeEmailScreen
import com.xeniac.warrantyroster_manager.feature_change_password.presentation.ChangePasswordScreen
import com.xeniac.warrantyroster_manager.feature_settings.presentation.SettingsScreen

fun NavGraphBuilder.settingsNavGraph(
    baseNavController: NavHostController,
    bottomPadding: Dp,
    userViewModel: UserViewModel
) {
    composable<SettingsScreen> {
        SettingsScreen(
            bottomPadding = bottomPadding,
            userViewModel = userViewModel,
            onNavigateToScreen = { destinationScreen ->
                baseNavController.navigate(route = destinationScreen)
            }
        )
    }

    composable<LinkedAccountsScreen> {
        /**
         * const val ERROR_FIREBASE_AUTH_ALREADY_LINKED = "User has already been linked to the given provider"
         */

        Text(
            text = "LinkedAccountsScreen",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }

    composable<ChangeEmailScreen> {
        ChangeEmailScreen(
            userViewModel = userViewModel,
            onNavigateUp = baseNavController::navigateUp
        )
    }

    composable<ChangePasswordScreen> {
        ChangePasswordScreen(
            userViewModel = userViewModel,
            onNavigateUp = baseNavController::navigateUp
        )
    }
}