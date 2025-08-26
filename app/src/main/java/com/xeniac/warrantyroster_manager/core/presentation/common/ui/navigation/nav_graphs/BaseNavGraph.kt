package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.SettingsScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantiesScreen

@Composable
fun SetupBaseNavGraph(
    homeNavController: NavHostController,
    bottomPadding: Dp
) {
    NavHost(
        navController = homeNavController,
        startDestination = WarrantiesScreen
    ) {
        composable<WarrantiesScreen> {
            Text(
                text = "WarrantiesScreen",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }

        composable<SettingsScreen> {
            Text(
                text = "Settings Screen",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
//            SettingsScreen(bottomPadding = bottomPadding)
        }
    }
}