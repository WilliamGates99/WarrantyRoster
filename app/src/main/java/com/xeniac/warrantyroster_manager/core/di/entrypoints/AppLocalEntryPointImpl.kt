package com.xeniac.warrantyroster_manager.core.di.entrypoints

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import dagger.hilt.EntryPoints

private lateinit var appLocalEntryPoint: AppLocalEntryPoint

@Composable
fun requireCurrentAppLocale(): AppLocale {
    if (!::appLocalEntryPoint.isInitialized) {
        appLocalEntryPoint = EntryPoints.get(
            /* component = */ LocalContext.current.applicationContext,
            /* entryPoint = */ AppLocalEntryPoint::class.java
        )
    }
    return appLocalEntryPoint.currentAppLocale
}