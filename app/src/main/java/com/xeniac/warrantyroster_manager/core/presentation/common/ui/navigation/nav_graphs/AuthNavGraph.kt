package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ForgotPwScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LoginScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.RegisterScreen
import com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation.ForgotPwScreen
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginScreen
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.RegisterScreen

@Composable
fun SetupAuthNavGraph(
    rootNavController: NavHostController,
    authNavController: NavHostController,
    bottomPadding: Dp
) {
    NavHost(
        navController = authNavController,
        startDestination = LoginScreen
    ) {
        composable<LoginScreen> {
            LoginScreen(
                onNavigateUp = {

                }
            )
        }

        composable<RegisterScreen> {
            RegisterScreen(
                onNavigateUp = {

                }
            )
        }

        composable<ForgotPwScreen> {
            ForgotPwScreen(
                onNavigateUp = {

                }
            )
        }
    }
}