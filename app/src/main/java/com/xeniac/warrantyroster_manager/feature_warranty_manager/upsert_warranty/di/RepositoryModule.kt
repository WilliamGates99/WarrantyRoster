package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.repositories.AddWarrantyRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.repositories.EditWarrantyRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.AddWarrantyRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.EditWarrantyRepository
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
    abstract fun bindAddWarrantyRepository(
        addWarrantyRepositoryImpl: AddWarrantyRepositoryImpl
    ): AddWarrantyRepository

    @Binds
    @ViewModelScoped
    abstract fun bindEditWarrantyRepository(
        editWarrantyRepositoryImpl: EditWarrantyRepositoryImpl
    ): EditWarrantyRepository
}