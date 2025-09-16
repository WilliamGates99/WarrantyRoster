package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.use_cases.UpsertWarrantyUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object UpsertWarrantyModule {

    @Provides
    @ViewModelScoped
    fun provideUpsertWarrantyUseCases(
        observeCategoriesUseCase: ObserveCategoriesUseCase,
    ): UpsertWarrantyUseCases = UpsertWarrantyUseCases(
        { observeCategoriesUseCase },
    )
}