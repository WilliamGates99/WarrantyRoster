package com.xeniac.warrantyroster_manager.di

import com.xeniac.warrantyroster_manager.warranty_management.data.repository.WarrantyRepositoryImpl
import com.xeniac.warrantyroster_manager.core.data.repository.PreferencesRepositoryImpl
import com.xeniac.warrantyroster_manager.core.data.repository.UserRepositoryImpl
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
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
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindMainRepository(
        mainRepositoryImp: WarrantyRepositoryImpl
    ): WarrantyRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository
}