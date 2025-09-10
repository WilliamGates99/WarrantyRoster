package com.xeniac.warrantyroster_manager.feature_auth.common.di

import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGithubRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.AuthUseCases
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithGithubUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGithubUseCase
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
    fun provideGetGoogleCredentialUseCase(
        loginWithGoogleRepository: LoginWithGoogleRepository
    ): GetGoogleCredentialUseCase = GetGoogleCredentialUseCase(loginWithGoogleRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithGoogleUseCase(
        loginWithGoogleRepository: LoginWithGoogleRepository
    ): LoginWithGoogleUseCase = LoginWithGoogleUseCase(loginWithGoogleRepository)

    @Provides
    @ViewModelScoped
    fun provideCheckPendingLoginWithXUseCase(
        loginWithXRepository: LoginWithXRepository
    ): CheckPendingLoginWithXUseCase = CheckPendingLoginWithXUseCase(loginWithXRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithXUseCase(
        loginWithXRepository: LoginWithXRepository
    ): LoginWithXUseCase = LoginWithXUseCase(loginWithXRepository)

    @Provides
    @ViewModelScoped
    fun provideCheckPendingLoginWithGithubUseCase(
        loginWithGithubRepository: LoginWithGithubRepository
    ): CheckPendingLoginWithGithubUseCase = CheckPendingLoginWithGithubUseCase(
        loginWithGithubRepository
    )

    @Provides
    @ViewModelScoped
    fun provideLoginWithGithubUseCase(
        loginWithGithubRepository: LoginWithGithubRepository
    ): LoginWithGithubUseCase = LoginWithGithubUseCase(loginWithGithubRepository)

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