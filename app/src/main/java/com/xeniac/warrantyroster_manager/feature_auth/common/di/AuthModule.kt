package com.xeniac.warrantyroster_manager.feature_auth.common.di

import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithFacebookRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.AuthUseCases
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithFacebookUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
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
    fun provideLoginWithGoogleUseCase(
        loginWithGoogleRepository: LoginWithGoogleRepository
    ): LoginWithGoogleUseCase = LoginWithGoogleUseCase(loginWithGoogleRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithXUseCase(
        loginWithXRepository: LoginWithXRepository
    ): LoginWithXUseCase = LoginWithXUseCase(loginWithXRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithFacebookUseCase(
        loginWithFacebookRepository: LoginWithFacebookRepository
    ): LoginWithFacebookUseCase = LoginWithFacebookUseCase(loginWithFacebookRepository)

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