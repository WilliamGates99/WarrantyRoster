package com.xeniac.warrantyroster_manager.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.xeniac.warrantyroster_manager.repositories.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    @TestDataStore
    @ExperimentalCoroutinesApi
    fun provideTestDataStore(@ApplicationContext context: Context) =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings_test") }
        )

    @Provides
    @Singleton
    @TestPreferencesRepository
    fun provideTestPreferencesRepository(@TestDataStore testDataStore: DataStore<Preferences>) =
        PreferencesRepository(testDataStore)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestPreferencesRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestDataStore