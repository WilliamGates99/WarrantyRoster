package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.di

import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.use_cases.ObserveCategoriesUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases.ObserveWarrantiesUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases.SearchWarrantiesUseCase
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.use_cases.WarrantiesUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object WarrantiesModule {

    @Provides
    @ViewModelScoped
    fun provideObserveWarrantiesUseCase(
        warrantiesRepository: WarrantiesRepository
    ): ObserveWarrantiesUseCase = ObserveWarrantiesUseCase(warrantiesRepository)

    @Provides
    @ViewModelScoped
    fun provideSearchWarrantiesUseCase(
        warrantiesRepository: WarrantiesRepository
    ): SearchWarrantiesUseCase = SearchWarrantiesUseCase(warrantiesRepository)

    @Provides
    @ViewModelScoped
    fun provideWarrantiesUseCases(
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        observeWarrantiesUseCase: ObserveWarrantiesUseCase,
        searchWarrantiesUseCase: SearchWarrantiesUseCase
    ): WarrantiesUseCases = WarrantiesUseCases(
        { observeCategoriesUseCase },
        { observeWarrantiesUseCase },
        { searchWarrantiesUseCase }
    )
}