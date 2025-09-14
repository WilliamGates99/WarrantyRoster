package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xeniac.warrantyroster_manager.core.presentation.common.UserViewModel
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty

@Composable
fun WarrantyDetailsScreen(
    userViewModel: UserViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToUpsertWarrantyScreen: (warranty: Warranty) -> Unit,
    viewModel: WarrantyDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Text(
        text = """
                    WarrantyDetailsScreen
                    warranty = ${state.warranty}}
                """.trimIndent(),
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .clickable(
                role = Role.Button,
                onClick = { onNavigateToUpsertWarrantyScreen(state.warranty) }
            )
    )
}