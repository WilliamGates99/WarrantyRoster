package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.BaseScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.OnboardingScreen

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
            Text(
                text = "Onboarding Screen",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
//            OnboardingScreen(
//                onNavigateToBaseScreen = {
//                    rootNavController.navigate(BaseScreen) {
//                        popUpTo(OnboardingScreen) {
//                            inclusive = true
//                        }
//                    }
//                }
//            )
        }

        composable<BaseScreen> {
//            HomeScreen()
            Text(
                text = "Base Screen",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    }
}