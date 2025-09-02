package com.xeniac.warrantyroster_manager.feature_auth.common.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.core.domain.models.AppLocale
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithFacebookRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.AuthUseCases
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithFacebookUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object AuthModule {

    @Provides
    @ViewModelScoped
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager = CredentialManager.create(context)

    @Provides
    @ViewModelScoped
    fun provideGoogleIdOption(): GetGoogleIdOption = GetGoogleIdOption.Builder().apply {
        setServerClientId(BuildConfig.AUTH_GOOGLE_SERVER_CLIENT_ID)
        setFilterByAuthorizedAccounts(false) // Only show accounts previously used to sign in
        setAutoSelectEnabled(false)
        setRequestVerifiedPhoneNumber(false)
    }.build()

    @Provides
    @ViewModelScoped
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
    @ViewModelScoped
    fun provideGetGoogleCredentialUseCase(
        loginWithGoogleRepository: LoginWithGoogleRepository
    ): GetGoogleCredentialUseCase = GetGoogleCredentialUseCase(loginWithGoogleRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithGoogleUseCase(
        loginWithGoogleRepository: LoginWithGoogleRepository
    ): LoginWithGoogleUseCase = LoginWithGoogleUseCase(loginWithGoogleRepository)

    @Provides
    @ViewModelScoped
    fun provideCheckPendingLoginWithXUseCase(
        loginWithXRepository: LoginWithXRepository
    ): CheckPendingLoginWithXUseCase = CheckPendingLoginWithXUseCase(loginWithXRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithXUseCase(
        loginWithXRepository: LoginWithXRepository
    ): LoginWithXUseCase = LoginWithXUseCase(loginWithXRepository)

    @Provides
    @ViewModelScoped
    fun provideLoginWithFacebookUseCase(
        loginWithFacebookRepository: LoginWithFacebookRepository
    ): LoginWithFacebookUseCase = LoginWithFacebookUseCase(loginWithFacebookRepository)

    @Provides
    @ViewModelScoped
    fun provideAuthUseCases(
        getCurrentAppLocaleUseCase: GetCurrentAppLocaleUseCase,
        storeCurrentAppLocaleUseCase: StoreCurrentAppLocaleUseCase
    ): AuthUseCases = AuthUseCases(
        { getCurrentAppLocaleUseCase },
        { storeCurrentAppLocaleUseCase }
    )
}