package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
//            BaseScreen()
            val firebaseAuth by remember { mutableStateOf(Firebase.auth) }

            Text(
                text = """
                    Base Screen
                    Signed in user:
                    ${firebaseAuth.currentUser ?: "null user"}
                """.trimIndent(),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    }
}