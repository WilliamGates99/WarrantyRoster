package com.xeniac.warrantyroster_manager.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.repositories.*
import com.xeniac.warrantyroster_manager.repositories.DefaultPreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Constants.COLLECTION_CATEGORIES
import com.xeniac.warrantyroster_manager.utils.Constants.COLLECTION_WARRANTIES
import com.xeniac.warrantyroster_manager.utils.Constants.DATASTORE_NAME_SETTINGS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    @CategoriesCollection
    fun provideFirestoreCategoriesCollectionRef() =
        Firebase.firestore.collection(COLLECTION_CATEGORIES)

    @Singleton
    @Provides
    @WarrantiesCollection
    fun provideFirestoreWarrantiesCollectionRef() =
        Firebase.firestore.collection(COLLECTION_WARRANTIES)

    @Singleton
    @Provides
    fun provideUserRepository(firebaseAuth: FirebaseAuth) =
        DefaultUserRepository(firebaseAuth) as UserRepository

    @Singleton
    @Provides
    fun provideDefaultMainRepository(
        firebaseAuth: FirebaseAuth,
        @CategoriesCollection categoriesCollectionRef: CollectionReference,
        @WarrantiesCollection warrantiesCollectionRef: CollectionReference
    ) = MainRepository(firebaseAuth, categoriesCollectionRef, warrantiesCollectionRef)

    @Singleton
    @Provides
    fun providePreferencesRepository(settingsDataStore: DataStore<Preferences>) =
        DefaultPreferencesRepository(settingsDataStore) as PreferencesRepository

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME_SETTINGS) }
        )

    @Singleton
    @Provides
    fun provideDecimalFormat() = DecimalFormat("00")

    @Singleton
    @Provides
    fun provideDateFormat() = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())

    @Singleton
    @Provides
    fun provideCoilImageLoader(@ApplicationContext context: Context) =
        ImageLoader.Builder(context).apply {
            components { add(SvgDecoder.Factory()) }
            diskCache {
                DiskCache.Builder()
                    // Set cache directory folder name
                    .directory(context.cacheDir.resolve("image_cache"))
                    .build()
            }
            memoryCache {
                MemoryCache.Builder(context)
                    // Set the max size to 25% of the app's available memory.
                    .maxSizePercent(0.25)
                    .build()
            }
            okHttpClient {
                // Don't limit concurrent network requests by host.
                val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                // Lazily create the OkHttpClient that is used for network operations.
                OkHttpClient.Builder()
                    .dispatcher(dispatcher)
                    .build()
            }
            // Ignore the network cache headers and always read from/write to the disk cache.
            respectCacheHeaders(false)
            crossfade(true)
            if (BuildConfig.DEBUG) logger(DebugLogger())
        }.build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoriesCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WarrantiesCollection