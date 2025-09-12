package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.repositories.CategoriesRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.repositories.CategoriesRepository
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
    abstract fun bindCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository
}