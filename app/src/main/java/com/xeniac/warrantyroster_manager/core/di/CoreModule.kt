package com.xeniac.warrantyroster_manager.core.di

import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetIsUserLoggedInUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.MainUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object CoreModule {

    @Provides
    @ViewModelScoped
    fun provideGetCurrentAppLocaleUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): GetCurrentAppLocaleUseCase = GetCurrentAppLocaleUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideGetIsUserLoggedInUseCase(
        warrantyRosterDataStoreRepository: WarrantyRosterDataStoreRepository
    ): GetIsUserLoggedInUseCase = GetIsUserLoggedInUseCase(warrantyRosterDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideMainUseCases(
        getCurrentAppLocaleUseCase: GetCurrentAppLocaleUseCase,
        getIsUserLoggedInUseCase: GetIsUserLoggedInUseCase
    ): MainUseCases = MainUseCases(
        { getCurrentAppLocaleUseCase },
        { getIsUserLoggedInUseCase }
    )
}