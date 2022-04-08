package com.xeniac.warrantyroster_manager.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
import com.xeniac.warrantyroster_manager.repositories.MainRepository
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.COLLECTION_CATEGORIES
import com.xeniac.warrantyroster_manager.utils.Constants.COLLECTION_WARRANTIES
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_SETTINGS
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LANGUAGE_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_COUNTRY_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_THEME_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

    @CategoriesCollection
    @Singleton
    @Provides
    fun provideFirestoreCategoriesCollectionRef() =
        Firebase.firestore.collection(COLLECTION_CATEGORIES)

    @WarrantiesCollection
    @Singleton
    @Provides
    fun provideFirestoreWarrantiesCollectionRef() =
        Firebase.firestore.collection(COLLECTION_WARRANTIES)

    @Singleton
    @Provides
    fun provideUserRepository(firebaseAuth: FirebaseAuth) = UserRepository(firebaseAuth)

    @Singleton
    @Provides
    fun provideMainRepository(
        firebaseAuth: FirebaseAuth,
        @CategoriesCollection categoriesCollectionRef: CollectionReference,
        @WarrantiesCollection warrantiesCollectionRef: CollectionReference
    ) = MainRepository(firebaseAuth, categoriesCollectionRef, warrantiesCollectionRef)

    @LoginPrefs
    @Singleton
    @Provides
    fun provideLoginPrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCE_LOGIN, MODE_PRIVATE)

    @SettingsPrefs
    @Singleton
    @Provides
    fun provideSettingsPrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCE_SETTINGS, MODE_PRIVATE)

    @Provides
    fun provideIsUserLoggedIn(@LoginPrefs loginPrefs: SharedPreferences) =
        loginPrefs.getBoolean(PREFERENCE_IS_LOGGED_IN_KEY, false)

    @CurrentLanguage
    @Singleton //TODO REMOVE AFTER ADDING PERSIAN
    @Provides
    fun provideCurrentLanguage(@SettingsPrefs settingsPrefs: SharedPreferences) =
        settingsPrefs.getString(PREFERENCE_LANGUAGE_KEY, "en") ?: "en"

    @CurrentCountry
    @Singleton //TODO REMOVE AFTER ADDING PERSIAN
    @Provides
    fun provideCurrentCountry(@SettingsPrefs settingsPrefs: SharedPreferences) =
        settingsPrefs.getString(PREFERENCE_COUNTRY_KEY, "US") ?: "US"

    @Provides
    fun provideCurrentTheme(@SettingsPrefs settingsPrefs: SharedPreferences) =
        settingsPrefs.getInt(PREFERENCE_THEME_KEY, 0)

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

    @CategoryTitleMapKey
    @Singleton //TODO REMOVE AFTER ADDING PERSIAN
    @Provides
    fun provideCategoryTitleMapKey(
        @CurrentLanguage currentLanguage: String,
        @CurrentCountry currentCountry: String
    ) = "${currentLanguage}-${currentCountry}"
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoriesCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WarrantiesCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoginPrefs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsPrefs

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrentLanguage

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CurrentCountry

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoryTitleMapKey