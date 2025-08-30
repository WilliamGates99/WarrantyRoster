package com.xeniac.warrantyroster_manager.core.di

import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetIsUserLoggedInUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.MainUseCases
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.PasswordStrengthHelper
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
    fun providePasswordStrengthHelper(): PasswordStrengthHelper = PasswordStrengthHelper()

    @Provides
    @ViewModelScoped
    fun provideGetCurrentAppLocaleUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): GetCurrentAppLocaleUseCase = GetCurrentAppLocaleUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideStoreCurrentAppLocaleUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): StoreCurrentAppLocaleUseCase = StoreCurrentAppLocaleUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideGetIsUserLoggedInUseCase(
        firebaseAuth: FirebaseAuth,
        warrantyRosterDataStoreRepository: WarrantyRosterDataStoreRepository
    ): GetIsUserLoggedInUseCase = GetIsUserLoggedInUseCase(
        firebaseAuth,
        warrantyRosterDataStoreRepository
    )

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