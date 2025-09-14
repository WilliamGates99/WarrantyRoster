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
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils.WarrantyNavType
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.WarrantiesScreen
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation.WarrantyDetailsScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.warrantiesNavGraph(
    baseNavController: NavHostController,
    bottomPadding: Dp,
    userViewModel: UserViewModel
) {
    composable<WarrantiesScreen> {
        WarrantiesScreen(
            bottomPadding = bottomPadding,
            userViewModel = userViewModel,
            onNavigateToWarrantyDetailsScreen = { warranty ->
                baseNavController.navigate(WarrantyDetailsScreen(warranty = warranty))
            }
        )
    }

    composable<WarrantyDetailsScreen>(
        typeMap = mapOf(typeOf<Warranty>() to WarrantyNavType)
    ) {
        WarrantyDetailsScreen(
            userViewModel = userViewModel,
            onNavigateUp = baseNavController::navigateUp,
            onNavigateToUpsertWarrantyScreen = { warranty ->
                baseNavController.navigate(UpsertWarrantyScreen(updatingWarranty = warranty))
            }
        )
    }

    composable<UpsertWarrantyScreen>(
        typeMap = mapOf(typeOf<Warranty?>() to WarrantyNavType)
    ) {
        Text(
            text = """
                    UpsertWarrantyScreen
                    updatingWarranty = ${it.toRoute<UpsertWarrantyScreen>().updatingWarranty}
                """.trimIndent(),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }
}