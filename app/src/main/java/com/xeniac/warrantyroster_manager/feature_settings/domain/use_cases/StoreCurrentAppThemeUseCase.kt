package com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_settings.domain.errors.StoreAppThemeError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreCurrentAppThemeUseCase(
    private val settingsDataStoreRepository: SettingsDataStoreRepository
) {
    operator fun invoke(
        newAppTheme: AppTheme
    ): Flow<Result<Unit, StoreAppThemeError>> = flow {
        return@flow try {
            settingsDataStoreRepository.storeCurrentAppTheme(newAppTheme)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(StoreAppThemeError.SomethingWentWrong))
        }
    }
}