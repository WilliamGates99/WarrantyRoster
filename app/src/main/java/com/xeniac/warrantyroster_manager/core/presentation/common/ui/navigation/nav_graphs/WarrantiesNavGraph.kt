package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.UpsertWarrantyScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantiesScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantyDetailsScreen
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.WarrantiesScreen

fun NavGraphBuilder.warrantiesNavGraph(
    baseNavController: NavHostController,
    bottomPadding: Dp,
    userViewModel: UserViewModel
) {
    composable<WarrantiesScreen> {
        WarrantiesScreen(
            bottomPadding = bottomPadding,
            userViewModel = userViewModel,
            onNavigateToUpsertWarrantyScreen = { warranty ->
                baseNavController.navigate(WarrantyDetailsScreen(warranty = warranty))
            }
        )
    }

    composable<WarrantyDetailsScreen>(
        // ADD CUSTOM TYPE MAP FOR WARRANTY
        // typeMap =
    ) {
        Text(
            text = """
                    WarrantyDetailsScreen
                    warranty = ${it.toRoute<WarrantyDetailsScreen>().warranty}
                """.trimIndent(),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }

    composable<UpsertWarrantyScreen>(
        // ADD CUSTOM TYPE MAP FOR WARRANTY
        // typeMap =
    ) {
        Text(
            text = """
                    UpsertWarrantyScreen
                    updatingWarrantyId = ${it.toRoute<UpsertWarrantyScreen>().updatingWarranty}
                """.trimIndent(),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )

        /** TODO: TEMP - REMOVE
         * const val ERROR_EMPTY_CATEGORY_LIST = "Category list is empty"
         * const val ERROR_EMPTY_WARRANTY_LIST = "Warranty list is empty"
         * const val ERROR_EMPTY_SEARCH_RESULT_LIST = "Search result list is empty"
         */
    }
}