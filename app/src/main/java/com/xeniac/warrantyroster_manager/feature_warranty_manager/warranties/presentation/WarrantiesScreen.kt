package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserAction
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.NetworkErrorMessage
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.EmptySearchResultMessage
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.EmptyWarrantiesMessage
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.WarrantiesList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantiesScreen(
    bottomPadding: Dp,
    userViewModel: UserViewModel,
    onNavigateToUpsertWarrantyScreen: (warranty: Warranty) -> Unit,
    viewModel: WarrantiesViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserverAsEvent(flow = viewModel.getWarrantiesEventChannel) { event ->
        when (event) {
            UiEvent.ForceLogoutUnauthorizedUser -> {
                userViewModel.onAction(UserAction.Logout)
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            CustomCenterAlignedTopAppBar(
                title = stringResource(id = R.string.warranties_app_bar_title),
                scrollBehavior = scrollBehavior,
                // TODO: ADD SEARCH ICON + SEARCH BAR
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .safeDrawingPadding()
        ) {
            if (with(state) { isCategoriesLoading || isWarrantiesLoading || isSearchWarrantiesLoading }) {
                // TODO: ANIMATED CONTENT FOR LOADING (SHIMMER EFFECT)
                Text(
                    text = "Loading",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
                return@Scaffold
            }

            state.searchErrorMessage?.let { searchErrorMessage ->
                NetworkErrorMessage(message = searchErrorMessage)
                return@Scaffold
            }

            state.errorMessage?.let { errorMessage ->
                NetworkErrorMessage(
                    message = errorMessage,
                    onRetryClick = { viewModel.onAction(WarrantiesAction.GetCategories) }
                )
                return@Scaffold
            }

            if (state.filteredWarranties?.isEmpty() == true) {
                EmptySearchResultMessage(searchQueryState = state.searchQueryState)
                return@Scaffold
            }

            if (state.warranties?.isEmpty() == true) {
                EmptyWarrantiesMessage()
                return@Scaffold
            }

            WarrantiesList(
                warranties = state.warranties,
                filteredWarranties = state.filteredWarranties,
                bottomPadding = bottomPadding,
                onNavigateToUpsertWarrantyScreen = onNavigateToUpsertWarrantyScreen
            )
        }
    }
}