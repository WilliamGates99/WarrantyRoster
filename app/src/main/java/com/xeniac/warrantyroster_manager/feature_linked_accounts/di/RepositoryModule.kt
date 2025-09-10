package com.xeniac.warrantyroster_manager.feature_linked_accounts.di

import com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories.LinkedAccountsRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories.UnlinkAccountsRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkedAccountsRepository
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.UnlinkAccountsRepository
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
    abstract fun bindLinkedAccountsRepository(
        linkedAccountsRepositoryImpl: LinkedAccountsRepositoryImpl
    ): LinkedAccountsRepository

    @Binds
    @ViewModelScoped
    abstract fun bindUnlinkAccountsRepository(
        unlinkAccountsRepositoryImpl: UnlinkAccountsRepositoryImpl
    ): UnlinkAccountsRepository
}