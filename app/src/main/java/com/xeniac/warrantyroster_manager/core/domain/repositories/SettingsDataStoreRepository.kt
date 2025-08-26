package com.xeniac.warrantyroster_manager.core.domain.repositories

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import kotlinx.coroutines.flow.Flow

typealias IsActivityRestartNeeded = Boolean

interface SettingsDataStoreRepository {

    fun getCurrentAppThemeSynchronously(): AppTheme

    fun getCurrentAppTheme(): Flow<AppTheme>

    suspend fun storeCurrentAppTheme(appTheme: AppTheme)

    fun getCurrentAppLocale(): AppLocale

    suspend fun storeCurrentAppLocale(newAppLocale: AppLocale): IsActivityRestartNeeded
}