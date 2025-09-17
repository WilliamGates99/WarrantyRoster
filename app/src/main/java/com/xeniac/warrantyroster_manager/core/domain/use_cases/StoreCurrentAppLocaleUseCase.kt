package com.xeniac.warrantyroster_manager.core.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.errors.StoreAppLocaleError
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.IsActivityRestartNeeded
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreCurrentAppLocaleUseCase(
    private val settingsDataStoreRepository: SettingsDataStoreRepository
) {
    operator fun invoke(
        newAppLocale: AppLocale
    ): Flow<Result<IsActivityRestartNeeded, StoreAppLocaleError>> = flow {
        return@flow try {
            emit(Result.Success(settingsDataStoreRepository.storeCurrentAppLocale(newAppLocale)))
        } catch (e: Exception) {
            emit(Result.Error(StoreAppLocaleError.SomethingWentWrong))
        }
    }
}