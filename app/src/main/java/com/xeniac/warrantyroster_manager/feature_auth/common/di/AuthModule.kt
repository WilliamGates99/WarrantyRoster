package com.xeniac.warrantyroster_manager.feature_auth.common.di

import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.AuthUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object AuthModule {

    @Provides
    @ViewModelScoped
    fun provideAuthUseCases(
        getCurrentAppLocaleUseCase: GetCurrentAppLocaleUseCase,
        storeCurrentAppLocaleUseCase: StoreCurrentAppLocaleUseCase
    ): AuthUseCases = AuthUseCases(
        { getCurrentAppLocaleUseCase },
        { storeCurrentAppLocaleUseCase }
    )
}