package com.xeniac.warrantyroster_manager.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @TestDataStore
    @ExperimentalCoroutinesApi
    fun provideTestDataStore(@ApplicationContext context: Context) =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings_test") }
        )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestDataStore