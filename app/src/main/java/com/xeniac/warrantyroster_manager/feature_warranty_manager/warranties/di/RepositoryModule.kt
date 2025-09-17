package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.data.repositories.WarrantiesRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
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
    abstract fun bindWarrantiesRepository(
        warrantiesRepositoryImpl: WarrantiesRepositoryImpl
    ): WarrantiesRepository
}