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
import androidx.navigation.toRoute
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.UpsertWarrantyScreen
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
        composable<WarrantiesScreen> {
            Text(
                text = "WarrantiesScreen",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }

        composable<UpsertWarrantyScreen>(
            // ADD CUSTOM TYPE MAP FOR WARRANTY
            // typeMap =
        ) {
            val updatingWarrantyId = it.toRoute<UpsertWarrantyScreen>().updatingWarrantyId

            Text(
                text = """
                    UpsertWarrantyScreen
                    updatingWarrantyId = $updatingWarrantyId
                """.trimIndent(),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }

        settingsNavGraph(
            baseNavController = baseNavController,
            bottomPadding = bottomPadding,
            userViewModel = userViewModel
        )
    }
}