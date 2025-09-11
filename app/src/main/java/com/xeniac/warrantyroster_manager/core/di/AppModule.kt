package com.xeniac.warrantyroster_manager.core.di

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.models.AppTheme
import com.xeniac.warrantyroster_manager.core.domain.models.MiscellaneousPreferences
import com.xeniac.warrantyroster_manager.core.domain.models.MiscellaneousPreferencesSerializer
import com.xeniac.warrantyroster_manager.core.domain.models.PermissionsPreferences
import com.xeniac.warrantyroster_manager.core.domain.models.PermissionsPreferencesSerializer
import com.xeniac.warrantyroster_manager.core.domain.models.SettingsPreferences
import com.xeniac.warrantyroster_manager.core.domain.models.SettingsPreferencesSerializer
import com.xeniac.warrantyroster_manager.core.domain.models.WarrantyRosterPreferences
import com.xeniac.warrantyroster_manager.core.domain.models.WarrantyRosterPreferencesSerializer
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.serialization.json.Json
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager = context.getSystemService(NotificationManager::class.java)

    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context,
        json: Json
    ): HttpClient = HttpClient(engineFactory = OkHttp) {
        expectSuccess = true

        install(Logging) {
            logger = Logger.ANDROID
            level = if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.NONE
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        install(HttpCache) {
            val cacheDir = context.cacheDir.resolve(relative = "ktor_cache")
            privateStorage(storage = FileStorage(directory = cacheDir))
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpRequestRetry) {
            retryOnExceptionOrServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 30_000 // 30 seconds
            requestTimeoutMillis = 30_000 // 30 seconds
            socketTimeoutMillis = 30_000 // 30 seconds
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    @Provides
    @Singleton
    fun providePermissionDataStore(
        @ApplicationContext context: Context
    ): DataStore<PermissionsPreferences> = synchronized(lock = SynchronizedObject()) {
        DataStoreFactory.create(
            serializer = PermissionsPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { PermissionsPreferences() },
            scope = CoroutineScope(context = Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(name = "Permissions.pb") }
        )
    }

    @OptIn(InternalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<SettingsPreferences> = synchronized(lock = SynchronizedObject()) {
        DataStoreFactory.create(
            serializer = SettingsPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { SettingsPreferences() },
            scope = CoroutineScope(context = Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(name = "Settings.pb") }
        )
    }

    @OptIn(InternalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideMiscellaneousDataStore(
        @ApplicationContext context: Context
    ): DataStore<MiscellaneousPreferences> = synchronized(lock = SynchronizedObject()) {
        DataStoreFactory.create(
            serializer = MiscellaneousPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { MiscellaneousPreferences() },
            scope = CoroutineScope(context = Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(name = "Miscellaneous.pb") }
        )
    }

    @OptIn(InternalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideWarrantyRosterDataStore(
        @ApplicationContext context: Context
    ): DataStore<WarrantyRosterPreferences> = synchronized(lock = SynchronizedObject()) {
        DataStoreFactory.create(
            serializer = WarrantyRosterPreferencesSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler { WarrantyRosterPreferences() },
            scope = CoroutineScope(context = Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(name = "WarrantyRoster.pb") }
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    @CategoriesCollection
    fun provideFirestoreCategoriesCollectionRef(
        firestore: FirebaseFirestore
    ): CollectionReference = firestore.collection(/* collectionPath = */ "categories")

    @Provides
    @Singleton
    @WarrantiesCollection
    fun provideFirestoreWarrantiesCollectionRef(
        firestore: FirebaseFirestore
    ): CollectionReference = firestore.collection(/* collectionPath = */ "warranties")

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager = CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideGoogleIdOption(): GetGoogleIdOption = GetGoogleIdOption.Builder().apply {
        setServerClientId(BuildConfig.AUTH_GOOGLE_SERVER_CLIENT_ID)
        setFilterByAuthorizedAccounts(false) // Only show accounts previously used to sign in
        setAutoSelectEnabled(false)
        setRequestVerifiedPhoneNumber(false)
    }.build()

    @Provides
    @Singleton
    @XQualifier
    fun provideXOAuthProvider(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): OAuthProvider = OAuthProvider.newBuilder("twitter.com").apply {
        val currentAppLocale = settingsDataStoreRepository.getCurrentAppLocale()
        val xWebsiteLanguage = when (currentAppLocale) {
            AppLocale.DEFAULT -> "en"
            AppLocale.ENGLISH_US -> "en"
            AppLocale.ENGLISH_GB -> "en"
            AppLocale.FARSI_IR -> "fa"
        }
        addCustomParameter("lang", xWebsiteLanguage)
    }.build()

    @Provides
    @Singleton
    @GithubQualifier
    fun provideGitHubOAuthProvider(
    ): OAuthProvider = OAuthProvider.newBuilder("github.com").apply {
        addCustomParameter("allow_signup", "true") // Default = True
        scopes = scopes + listOf(
            "read:user",
            "user:email"
        )
    }.build()

    @Provides
    fun provideAppTheme(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): AppTheme = settingsDataStoreRepository.getCurrentAppThemeSynchronously()

    @Provides
    @Singleton
    fun provideDecimalFormat(): DecimalFormat = DecimalFormat(
        /* pattern = */ "00",
        /* symbols = */ DecimalFormatSymbols(Locale.US)
    )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class XQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GithubQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoriesCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WarrantiesCollection