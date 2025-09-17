package com.xeniac.warrantyroster_manager.feature_settings.di

import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.feature_settings.domain.repositories.AccountVerificationRepository
import com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases.GetCurrentAppThemeUseCase
import com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases.SendVerificationEmailUseCase
import com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases.SettingsUseCases
import com.xeniac.warrantyroster_manager.feature_settings.domain.use_cases.StoreCurrentAppThemeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object SettingsModule {

    @Provides
    @ViewModelScoped
    fun provideGetCurrentAppThemeUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): GetCurrentAppThemeUseCase = GetCurrentAppThemeUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideStoreCurrentAppThemeUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): StoreCurrentAppThemeUseCase = StoreCurrentAppThemeUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideSendVerificationEmailUseCase(
        accountVerificationRepository: AccountVerificationRepository
    ): SendVerificationEmailUseCase = SendVerificationEmailUseCase(accountVerificationRepository)

    @Provides
    @ViewModelScoped
    fun provideSettingsUseCases(
        getCurrentAppLocaleUseCase: GetCurrentAppLocaleUseCase,
        getCurrentAppThemeUseCase: GetCurrentAppThemeUseCase,
        storeCurrentAppLocaleUseCase: StoreCurrentAppLocaleUseCase,
        storeCurrentAppThemeUseCase: StoreCurrentAppThemeUseCase,
        sendVerificationEmailUseCase: SendVerificationEmailUseCase
    ): SettingsUseCases = SettingsUseCases(
        { getCurrentAppLocaleUseCase },
        { getCurrentAppThemeUseCase },
        { storeCurrentAppLocaleUseCase },
        { storeCurrentAppThemeUseCase },
        { sendVerificationEmailUseCase }
    )
}