package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedHiltViewModel(
    navController: NavHostController,
    route: Any
): T {
    val parentEntry = remember(key1 = this) { navController.getBackStackEntry(route) }
    return hiltViewModel(viewModelStoreOwner = parentEntry)
}