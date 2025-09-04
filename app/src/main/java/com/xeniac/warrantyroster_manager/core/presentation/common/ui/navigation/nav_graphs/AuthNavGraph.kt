package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.AuthScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.ForgotPasswordGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.LoginScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.RegisterScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils.sharedHiltViewModel
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.forgot_pw.ForgotPwScreen
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.presentation.reset_pw_instruction.ResetPwInstructionScreen
import com.xeniac.warrantyroster_manager.feature_auth.login.presentation.LoginScreen
import com.xeniac.warrantyroster_manager.feature_auth.register.presentation.RegisterScreen

@Composable
fun SetupAuthNavGraph(
    rootNavController: NavHostController,
    authNavController: NavHostController
) {
    NavHost(
        navController = authNavController,
        startDestination = LoginScreen,
        enterTransition = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 400)
            )
        },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 400)
            )
        },
        popEnterTransition = {
            slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 400)
            )
        },
        popExitTransition = {
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 400)
            )
        }
    ) {
        composable<LoginScreen> {
            LoginScreen(
                onNavigateToRegisterScreen = {
                    authNavController.navigate(RegisterScreen)
                },
                onNavigateToForgotPwScreen = {
                    authNavController.navigate(ForgotPasswordGraph)
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

        navigation<ForgotPasswordGraph>(
            startDestination = ForgotPasswordGraph.ForgotPwScreen
        ) {
            composable<ForgotPasswordGraph.ForgotPwScreen> { backStackEntry ->
                ForgotPwScreen(
                    viewModel = backStackEntry.sharedHiltViewModel(
                        navController = authNavController,
                        route = ForgotPasswordGraph
                    ),
                    onNavigateUp = authNavController::navigateUp,
                    onNavigateToResetPwInstructionScreen = {
                        authNavController.navigate(ForgotPasswordGraph.ResetPwInstructionScreen) {
                            popUpTo<ForgotPasswordGraph> {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable<ForgotPasswordGraph.ResetPwInstructionScreen> { backStackEntry ->
                ResetPwInstructionScreen(
                    viewModel = backStackEntry.sharedHiltViewModel(
                        navController = authNavController,
                        route = ForgotPasswordGraph
                    ),
                    onNavigateUp = authNavController::navigateUp
                )
            }
        }
    }
}