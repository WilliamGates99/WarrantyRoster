package com.xeniac.warrantyroster_manager.di

import com.xeniac.warrantyroster_manager.data.repository.MainRepositoryImp
import com.xeniac.warrantyroster_manager.data.repository.PreferencesRepositoryImp
import com.xeniac.warrantyroster_manager.data.repository.UserRepositoryImp
import com.xeniac.warrantyroster_manager.domain.repository.MainRepository
import com.xeniac.warrantyroster_manager.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImp: UserRepositoryImp
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindMainRepository(
        mainRepositoryImp: MainRepositoryImp
    ): MainRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImp: PreferencesRepositoryImp
    ): PreferencesRepository
}