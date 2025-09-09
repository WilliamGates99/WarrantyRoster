package com.xeniac.warrantyroster_manager.feature_change_email.di

import com.xeniac.warrantyroster_manager.feature_change_email.data.repositories.ChangeUserEmailRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_change_email.domain.repositories.ChangeUserEmailRepository
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
    abstract fun bindChangeUserEmailRepository(
        changeUserEmailRepositoryImpl: ChangeUserEmailRepositoryImpl
    ): ChangeUserEmailRepository
}