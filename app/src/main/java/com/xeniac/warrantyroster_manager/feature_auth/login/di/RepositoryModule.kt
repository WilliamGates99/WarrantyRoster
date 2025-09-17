package com.xeniac.warrantyroster_manager.feature_auth.login.di

import com.xeniac.warrantyroster_manager.feature_auth.login.data.repositories.LoginWithEmailRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.repositories.LoginWithEmailRepository
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
    abstract fun bindLoginWithEmailRepository(
        loginWithEmailRepositoryImpl: LoginWithEmailRepositoryImpl
    ): LoginWithEmailRepository
}