package com.xeniac.warrantyroster_manager.feature_base.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CradleCutoutShape
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.SettingsScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens.WarrantiesScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.Black
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicBlack
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.dynamicGrayDark
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toDp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.toPx

enum class NavigationBarItems(
    val destinationScreen: Any,
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int
) {
    WARRANTIES(
        destinationScreen = WarrantiesScreen,
        titleId = R.string.base_nav_warranties,
        iconId = R.drawable.ic_core_nav_warranties
    ),
    SETTINGS(
        destinationScreen = SettingsScreen,
        titleId = R.string.base_nav_settings,
        iconId = R.drawable.ic_core_nav_settings
    )
}

@Composable
fun CustomNavigationBar(
    isBottomAppBarVisible: Boolean,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onItemClick: (destinationScreen: Any) -> Unit,
    onAddWarrantyFabClick: () -> Unit
) {
    var navigationBarHeightPx by remember { mutableIntStateOf(0) }
    var fabSizePx by remember { mutableFloatStateOf(0f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        CradledNavigationBar(
            isVisible = isBottomAppBarVisible,
            fabSizePx = fabSizePx,
            currentDestination = currentDestination,
            onItemClick = onItemClick,
            onHeightChanged = { heightPx ->
                navigationBarHeightPx = heightPx
            }
        )

        GoalFab(
            isVisible = isBottomAppBarVisible,
            onClick = onAddWarrantyFabClick,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = -navigationBarHeightPx / 2
                    )
                }
                .onSizeChanged { fabSizePx = it.width.toFloat() }
        )
    }
}

@Composable
private fun CradledNavigationBar(
    isVisible: Boolean,
    fabSizePx: Float,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    enterTransition: EnterTransition = fadeIn() + slideInVertically(
        initialOffsetY = { it / 2 }
    ),
    exitTransition: ExitTransition = slideOutVertically(
        targetOffsetY = { it / 2 },
    ) + fadeOut(),
    shape: Shape = CradleCutoutShape(
        cutoutRadiusPx = fabSizePx,
        cradleSpacePx = 16.dp.toPx(),
        topStartCornerRadiusPx = 24.dp.toPx(),
        topEndCornerRadiusPx = 24.dp.toPx()
    ),
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 8.dp,
        vertical = 12.dp
    ),
    onItemClick: (destinationScreen: Any) -> Unit,
    onHeightChanged: (heightPx: Int) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { onHeightChanged(it.height) }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .dropShadow(
                        shape = shape,
                        shadow = Shadow(
                            radius = 4.dp,
                            color = Black,
                            alpha = 0.04f
                        )
                    )
                    .dropShadow(
                        shape = shape,
                        shadow = Shadow(
                            radius = 4.dp,
                            color = Black,
                            alpha = 0.20f
                        )
                    )
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .padding(contentPadding)
            ) {
                NavigationBarItemsRow(
                    currentDestination = currentDestination,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavigationBarItemsRow(
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors().copy(
        selectedIndicatorColor = Color.Transparent,
        selectedIconColor = MaterialTheme.colorScheme.dynamicBlack,
        selectedTextColor = MaterialTheme.colorScheme.dynamicBlack,
        unselectedIconColor = MaterialTheme.colorScheme.dynamicGrayDark,
        unselectedTextColor = MaterialTheme.colorScheme.dynamicGrayDark,
        disabledIconColor = MaterialTheme.colorScheme.dynamicGrayDark.copy(alpha = 0.38f),
        disabledTextColor = MaterialTheme.colorScheme.dynamicGrayDark.copy(alpha = 0.38f)

    ),
    onItemClick: (destinationScreen: Any) -> Unit
) {
    NavigationBarItems.entries.forEach { navItem ->
        var labelHeightPx by remember { mutableIntStateOf(0) }

        val isSelected = isNavItemSelected(
            navItem = navItem,
            currentDestination = currentDestination
        )

        NavigationBarItem(
            enabled = !isSelected,
            selected = isSelected,
            alwaysShowLabel = true,
            colors = colors,
            icon = {
                Icon(
                    painter = painterResource(id = navItem.iconId),
                    contentDescription = stringResource(id = navItem.titleId),
                    tint = when {
                        isSelected -> colors.selectedIconColor
                        else -> colors.unselectedIconColor
                    },
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(id = navItem.titleId),
                    style = LocalTextStyle.current.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = when {
                            isSelected -> colors.selectedTextColor
                            else -> colors.unselectedTextColor
                        }
                    ),
                    maxLines = 2,
                    modifier = Modifier.onSizeChanged {
                        labelHeightPx = it.height
                    }
                )
            },
            onClick = { onItemClick(navItem.destinationScreen) },
            modifier = modifier.height(24.dp + labelHeightPx.toDp() + 8.dp)
        )
    }
}

fun isNavItemSelected(
    navItem: NavigationBarItems,
    currentDestination: NavDestination?
): Boolean {
    if (currentDestination?.hierarchy == null) {
        return false
    }

    return currentDestination.hierarchy.any {
        it.hasRoute(route = navItem.destinationScreen::class)
    }
}