package com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.utils.EnableEdgeToEdgeWindow
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.utils.getContrastAwareColorScheme

@Composable
fun WarrantyRosterTheme(
    content: @Composable () -> Unit
) {
    EnableEdgeToEdgeWindow()

    MaterialTheme(
        colorScheme = getContrastAwareColorScheme(),
        typography = getTypography(),
        content = content
    )
}