package com.xeniac.warrantyroster_manager.core.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCurrentAppLocaleUseCase(
    private val settingsDataStoreRepository: SettingsDataStoreRepository
) {
    operator fun invoke(): Flow<AppLocale> = flow {
        return@flow emit(settingsDataStoreRepository.getCurrentAppLocale())
    }
}