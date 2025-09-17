package com.xeniac.warrantyroster_manager.feature_settings.di

import com.xeniac.warrantyroster_manager.feature_settings.data.repositories.AccountVerificationRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_settings.domain.repositories.AccountVerificationRepository
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
    abstract fun bindAccountVerificationRepository(
        accountVerificationRepositoryImpl: AccountVerificationRepositoryImpl
    ): AccountVerificationRepository
}