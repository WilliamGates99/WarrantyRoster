package com.xeniac.warrantyroster_manager.feature_base.di

import com.xeniac.warrantyroster_manager.feature_base.data.repositories.AppReviewRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_base.data.repositories.AppUpdateRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppReviewRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
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
    abstract fun bindAppUpdateRepository(
        appUpdateRepositoryImpl: AppUpdateRepositoryImpl
    ): AppUpdateRepository

    @Binds
    @ViewModelScoped
    abstract fun bindAppReviewRepository(
        appReviewRepositoryImpl: AppReviewRepositoryImpl
    ): AppReviewRepository
}