package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantiesScreen

@Composable
fun SetupBaseNavGraph(
    baseNavController: NavHostController,
    bottomPadding: Dp,
    userViewModel: UserViewModel
) {
    NavHost(
        navController = baseNavController,
        startDestination = WarrantiesScreen
    ) {
        warrantiesNavGraph(
            baseNavController = baseNavController,
            bottomPadding = bottomPadding,
            userViewModel = userViewModel
        )

        settingsNavGraph(
            baseNavController = baseNavController,
            bottomPadding = bottomPadding,
            userViewModel = userViewModel
        )
    }
}