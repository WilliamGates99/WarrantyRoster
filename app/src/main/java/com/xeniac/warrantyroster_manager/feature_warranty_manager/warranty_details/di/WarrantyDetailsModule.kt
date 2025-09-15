package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.repositories.DeleteWarrantyRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.use_cases.DeleteWarrantyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object WarrantyDetailsModule {

    @Provides
    @ViewModelScoped
    fun provideDeleteWarrantyUseCase(
        deleteWarrantyRepository: DeleteWarrantyRepository
    ): DeleteWarrantyUseCase = DeleteWarrantyUseCase(deleteWarrantyRepository)
}