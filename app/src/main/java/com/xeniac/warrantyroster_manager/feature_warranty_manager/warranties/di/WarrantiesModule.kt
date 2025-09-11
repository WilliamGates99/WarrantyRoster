package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.di

import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases.GetCurrentAppThemeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object WarrantiesModule {

//    @Provides
//    @ViewModelScoped
//    fun provideGetCurrentAppThemeUseCase(
//        settingsDataStoreRepository: SettingsDataStoreRepository
//    ): GetCurrentAppThemeUseCase = GetCurrentAppThemeUseCase(settingsDataStoreRepository)
}