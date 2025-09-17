package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.repositories.CategoriesRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object WarrantyManagerModule {

    @Provides
    @ViewModelScoped
    fun provideObserveCategoriesUseCase(
        categoriesRepository: CategoriesRepository
    ): ObserveCategoriesUseCase = ObserveCategoriesUseCase(categoriesRepository)
}