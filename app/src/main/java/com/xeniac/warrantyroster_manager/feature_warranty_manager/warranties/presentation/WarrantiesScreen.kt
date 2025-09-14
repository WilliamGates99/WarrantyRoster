package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.ContentLoadingAnimation
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBarWithSearchBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.NetworkErrorMessage
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.EmptySearchResultMessage
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.EmptyWarrantiesMessage
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.WarrantiesList
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components.WarrantiesListLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantiesScreen(
    bottomPadding: Dp,
    userViewModel: UserViewModel,
    onNavigateToWarrantyDetailsScreen: (warranty: Warranty) -> Unit,
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
            CustomCenterAlignedTopAppBarWithSearchBar(
                title = stringResource(id = R.string.warranties_app_bar_title),
                scrollBehavior = scrollBehavior,
                isSearchBarVisible = state.isSearchBarVisible,
                searchQueryState = state.searchQueryState,
                searchBarPlaceholder = stringResource(id = R.string.warranties_search_textfield_hint),
                onSearchClick = { viewModel.onAction(WarrantiesAction.ShowSearchBar) },
                onCloseSearchClick = { viewModel.onAction(WarrantiesAction.HideSearchBar) },
                onSearchValueChange = { newValue ->
                    viewModel.onAction(WarrantiesAction.SearchQueryChanged(newValue))
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        ContentLoadingAnimation(
            isContentLoading = with(state) { isCategoriesLoading || isWarrantiesLoading },
            loadingContent = {
                WarrantiesListLoading(
                    bottomPadding = bottomPadding
                )
            },
            content = {
                state.searchErrorMessage?.let { searchErrorMessage ->
                    NetworkErrorMessage(message = searchErrorMessage)
                    return@ContentLoadingAnimation
                }

                state.errorMessage?.let { errorMessage ->
                    NetworkErrorMessage(
                        message = errorMessage,
                        onRetryClick = { viewModel.onAction(WarrantiesAction.GetCategories) }
                    )
                    return@ContentLoadingAnimation
                }

                if (state.filteredWarranties?.isEmpty() == true) {
                    EmptySearchResultMessage(searchQueryState = state.searchQueryState)
                    return@ContentLoadingAnimation
                }

                if (state.warranties?.isEmpty() == true) {
                    EmptyWarrantiesMessage()
                    return@ContentLoadingAnimation
                }

                WarrantiesList(
                    warranties = state.warranties,
                    filteredWarranties = state.filteredWarranties,
                    bottomPadding = bottomPadding,
                    onNavigateToWarrantyDetailsScreen = onNavigateToWarrantyDetailsScreen
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .safeDrawingPadding()
        )
    }
}