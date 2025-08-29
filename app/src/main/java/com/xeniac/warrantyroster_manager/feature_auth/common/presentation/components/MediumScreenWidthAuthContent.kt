package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupAuthNavGraph

@Composable
fun MediumScreenWidthAuthContent(
    rootNavController: NavHostController,
    authNavController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
        // .padding(innerPadding)
    ) {
        SetupAuthNavGraph(
            rootNavController = rootNavController,
            authNavController = authNavController,
            bottomPadding = innerPadding.calculateBottomPadding()
        )
    }
}