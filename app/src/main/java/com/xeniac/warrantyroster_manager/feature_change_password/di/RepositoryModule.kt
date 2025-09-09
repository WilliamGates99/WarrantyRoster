package com.xeniac.warrantyroster_manager.feature_change_password.di

import com.xeniac.warrantyroster_manager.feature_change_password.data.repositories.ChangeUserPasswordRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_change_password.domain.repositories.ChangeUserPasswordRepository
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
    abstract fun bindChangeUserPasswordRepository(
        changeUserPasswordRepositoryImpl: ChangeUserPasswordRepositoryImpl
    ): ChangeUserPasswordRepository
}