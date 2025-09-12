package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.common.UserAction
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.CustomCenterAlignedTopAppBar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.SwipeableSnackbar
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addPaddingValues
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ObserverAsEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiEvent
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.findActivity
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantiesScreen(
    bottomPadding: Dp,
    userViewModel: UserViewModel,
    onNavigateToUpsertWarrantyScreen: (warranty: Warranty) -> Unit,
    viewModel: WarrantiesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current ?: context.findActivity()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val horizontalPadding by remember { derivedStateOf { 8.dp } }
    val verticalPadding by remember { derivedStateOf { 24.dp } }

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
        snackbarHost = {
            SwipeableSnackbar(
                hostState = snackbarHostState,
                modifier = when {
                    bottomPadding > 0.dp -> Modifier.padding(bottom = bottomPadding)
                    else -> Modifier
                }
            )
        },
        topBar = {
            CustomCenterAlignedTopAppBar(
                title = stringResource(id = R.string.warranties_app_bar_title),
                scrollBehavior = scrollBehavior
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
            if (
                with(state) {
                    isCategoriesLoading || isWarrantiesLoading
                }
            ) {
                // TODO: ANIMATED CONTENT (SHIMMER EFFECT)
                Text(
                    text = "Loading",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
                return@Scaffold
            }

            state.warranties?.let { warranties ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                    contentPadding = PaddingValues(
                        bottom = when {
                            bottomPadding > 0.dp -> bottomPadding
                            else -> 0.dp
                        }
                    ).addPaddingValues(
                        horizontal = horizontalPadding,
                        vertical = verticalPadding
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = warranties,
                        key = { it.id }
                    ) { warranty ->
                        Text(
                            text = """
                                Title: ${warranty.title}
                                Category: ${warranty.category.title}
                            """.trimIndent(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = Role.Button,
                                    onClick = {
                                        onNavigateToUpsertWarrantyScreen(warranty)
                                    }
                                )
                                .padding(horizontal = horizontalPadding)
                        )
                    }
                }
            }
        }
    }
}