package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.data.repositories.DeleteWarrantyRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.repositories.DeleteWarrantyRepository
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
    abstract fun bindDeleteWarrantyRepository(
        deleteWarrantyRepositoryImpl: DeleteWarrantyRepositoryImpl
    ): DeleteWarrantyRepository
}