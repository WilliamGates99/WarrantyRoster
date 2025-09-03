package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.AuthScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ForgotPwInstructionScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ForgotPwScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LoginScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.RegisterScreen
import com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation.forgot_pw.ForgotPwScreen
import com.xeniac.warrantyroster_manager.feature_auth.forgot_pw.presentation.forgot_pw_instruction.ForgotPwInstructionScreen
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginScreen
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.RegisterScreen

@Composable
fun SetupAuthNavGraph(
    rootNavController: NavHostController,
    authNavController: NavHostController
) {
    // TODO: ANIMATED TRANSITION
    NavHost(
        navController = authNavController,
        startDestination = LoginScreen
    ) {
        composable<LoginScreen> {
            LoginScreen(
                onNavigateToRegisterScreen = {
                    authNavController.navigate(RegisterScreen)
                },
                onNavigateToForgotPwScreen = {
                    authNavController.navigate(ForgotPwScreen)
                },
                onNavigateToBaseScreen = {
                    rootNavController.navigate(BaseScreen) {
                        popUpTo(AuthScreen) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<RegisterScreen> {
            RegisterScreen(
                onNavigateUp = authNavController::navigateUp,
                onNavigateToBaseScreen = {
                    rootNavController.navigate(BaseScreen) {
                        popUpTo(AuthScreen) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<ForgotPwScreen> {
            ForgotPwScreen(
                onNavigateUp = authNavController::navigateUp
            )
        }

        composable<ForgotPwInstructionScreen> {
            ForgotPwInstructionScreen(
                onNavigateUp = authNavController::navigateUp
            )
        }
    }
}