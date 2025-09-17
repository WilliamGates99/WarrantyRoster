package com.xeniac.warrantyroster_manager.core.di.entrypoints

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppLocalEntryPoint {
    val currentAppLocale: AppLocale
}