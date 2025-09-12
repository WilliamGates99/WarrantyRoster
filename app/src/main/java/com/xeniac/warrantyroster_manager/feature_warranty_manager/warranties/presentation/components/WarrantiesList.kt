package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.utils.addPaddingValues
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty

@Composable
fun WarrantiesList(
    warranties: List<Warranty>?,
    filteredWarranties: List<Warranty>?,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    onNavigateToUpsertWarrantyScreen: (warranty: Warranty) -> Unit,
) {
    val horizontalPadding by remember { derivedStateOf { 8.dp } }
    val verticalPadding by remember { derivedStateOf { 24.dp } }

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
        modifier = modifier.fillMaxSize()
    ) {
        filteredWarranties?.let { filteredWarranties ->
            items(
                items = filteredWarranties,
                key = { it.id }
            ) { warranty ->
                WarrantyItem(
                    warranty = warranty,
                    onClick = { onNavigateToUpsertWarrantyScreen(warranty) }
                )
            }
            return@LazyColumn
        }

        warranties?.let { warranties ->
            items(
                items = warranties,
                key = { it.id }
            ) { warranty ->
                WarrantyItem(
                    warranty = warranty,
                    onClick = { onNavigateToUpsertWarrantyScreen(warranty) }
                )
            }
        }
    }
}

@Composable
private fun WarrantyItem(
    warranty: Warranty,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // TODO: IMPLEMENT
    Text(
        text = """
            Title: ${warranty.title}
            Category: ${warranty.category.title}
        """.trimIndent(),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
    )
}