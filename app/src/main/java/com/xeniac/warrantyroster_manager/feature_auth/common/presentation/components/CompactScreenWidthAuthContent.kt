package com.xeniac.warrantyroster_manager.feature_auth.common.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupAuthNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Red
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.White

@Composable
fun CompactScreenWidthAuthContent(
    rootNavController: NavHostController,
    authNavController: NavHostController,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(topStart = 64.dp),
    background: Color = White
) {
    val layoutDirection = LocalLayoutDirection.current

    val backStackEntry by authNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

//    LaunchedEffect(key1 = currentDestination) {
//        val test2 = when {
//            currentDestination == null -> "null"
//            currentDestination.hasRoute(LoginScreen::class) -> "login"
//            currentDestination.hasRoute(RegisterScreen::class) -> "register"
//            else -> "else"
//        }
//
//        Timber.i("test2 = $test2")
//    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection),
            )
    ) {
        Header()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(shape)
                .background(background)
        ) {
            SetupAuthNavGraph(
                rootNavController = rootNavController,
                authNavController = authNavController,
                bottomPadding = innerPadding.calculateBottomPadding()
            )
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter =,
                contentDescription =,
                modifier = Modifier.height(120.dp)
            )
        }

        // todo: locale icon
    }
}