package com.xeniac.warrantyroster_manager.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.xeniac.warrantyroster_manager.repositories.MainRepository
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_SETTINGS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
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
    @Provides
    fun provideCurrentLanguage(@SettingsPrefs settingsPrefs: SharedPreferences) =
        settingsPrefs.getString(Constants.PREFERENCE_LANGUAGE_KEY, "en") ?: "en"

    @CurrentCountry
    @Provides
    fun provideCurrentCountry(@SettingsPrefs settingsPrefs: SharedPreferences) =
        settingsPrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US") ?: "US"

    @Provides
    fun provideCurrentTheme(@SettingsPrefs settingsPrefs: SharedPreferences) =
        settingsPrefs.getInt(Constants.PREFERENCE_THEME_KEY, 0)
}

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