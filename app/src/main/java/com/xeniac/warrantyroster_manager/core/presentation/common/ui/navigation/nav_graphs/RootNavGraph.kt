package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xeniac.warrantyroster_manager.core.presentation.base_screen.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.AuthScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.OnboardingScreen
import com.xeniac.warrantyroster_manager.feature_auth.common.presentation.AuthScreen
import com.xeniac.warrantyroster_manager.feature_onboarding.presentation.OnboardingScreen

@Composable
fun SetupRootNavGraph(
    rootNavController: NavHostController,
    startDestination: Any
) {
    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {
        composable<OnboardingScreen> {
            OnboardingScreen(
                onNavigateToAuthScreen = {
                    rootNavController.navigate(AuthScreen) {
                        popUpTo(OnboardingScreen) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<AuthScreen> {
            AuthScreen(rootNavController = rootNavController)
        }

        composable<BaseScreen> {
            BaseScreen(
                onLogoutNavigate = {
                    rootNavController.navigate(AuthScreen) {
                        popUpTo<BaseScreen> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}