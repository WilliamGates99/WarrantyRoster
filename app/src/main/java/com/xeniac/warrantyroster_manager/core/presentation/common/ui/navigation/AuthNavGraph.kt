package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.AuthNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LoginScreen

fun NavGraphBuilder.authNavGraph(
    rootNavController: NavHostController
) {
    navigation<AuthNavGraph>(
        startDestination = LoginScreen
    ) {
        composable<LoginScreen> {
            Text(
                text = "Login Screen",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
                    .clickable {
                        rootNavController.navigate(BaseScreen) {
                            popUpTo(AuthNavGraph) {
                                inclusive = true
                            }
                        }
                    }
            )
        }
    }
}