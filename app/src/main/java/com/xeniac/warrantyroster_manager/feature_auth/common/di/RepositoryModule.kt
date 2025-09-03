package com.xeniac.warrantyroster_manager.feature_auth.common.di

import com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories.LoginWithGithubRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories.LoginWithGoogleRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories.LoginWithXRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGithubRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindLoginWithGoogleRepository(
        loginWithGoogleRepositoryImpl: LoginWithGoogleRepositoryImpl
    ): LoginWithGoogleRepository

    @Binds
    @ViewModelScoped
    abstract fun bindLoginWithXRepository(
        loginWithXRepositoryImpl: LoginWithXRepositoryImpl
    ): LoginWithXRepository

    @Binds
    @ViewModelScoped
    abstract fun bindLoginWithGithubRepository(
        loginWithGithubRepositoryImpl: LoginWithGithubRepositoryImpl
    ): LoginWithGithubRepository
}