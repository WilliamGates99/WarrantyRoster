package com.xeniac.warrantyroster_manager.di

import com.xeniac.warrantyroster_manager.repositories.MainRepository
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepository() = UserRepository()

    @Singleton
    @Provides
    fun provideMainRepository() = MainRepository()
}