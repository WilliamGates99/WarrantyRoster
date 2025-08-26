package com.xeniac.warrantyroster_manager.core.di

import com.xeniac.warrantyroster_manager.core.data.repositories.ConnectivityObserverImpl
import com.xeniac.warrantyroster_manager.core.data.repositories.MiscellaneousDataStoreRepositoryImpl
import com.xeniac.warrantyroster_manager.core.data.repositories.PermissionsDataStoreRepositoryImpl
import com.xeniac.warrantyroster_manager.core.data.repositories.SettingsDataStoreRepositoryImpl
import com.xeniac.warrantyroster_manager.core.data.repositories.WarrantyRosterDataStoreRepositoryImpl
import com.xeniac.warrantyroster_manager.core.domain.repositories.ConnectivityObserver
import com.xeniac.warrantyroster_manager.core.domain.repositories.MiscellaneousDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.PermissionsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
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
    abstract fun bindConnectivityObserver(
        connectivityObserverImpl: ConnectivityObserverImpl
    ): ConnectivityObserver

    @Binds
    @Singleton
    abstract fun bindPermissionsDataStoreRepository(
        permissionsDataStoreRepositoryImpl: PermissionsDataStoreRepositoryImpl
    ): PermissionsDataStoreRepository

    @Binds
    @Singleton
    abstract fun bindSettingsDataStoreRepository(
        settingsDataStoreRepositoryImpl: SettingsDataStoreRepositoryImpl
    ): SettingsDataStoreRepository

    @Binds
    @Singleton
    abstract fun bindMiscellaneousDataStoreRepository(
        miscellaneousDataStoreRepositoryImpl: MiscellaneousDataStoreRepositoryImpl
    ): MiscellaneousDataStoreRepository

    @Binds
    @Singleton
    abstract fun bindWarrantyRosterDataStoreRepository(
        dsfutDataStoreRepositoryImpl: WarrantyRosterDataStoreRepositoryImpl
    ): WarrantyRosterDataStoreRepository
}