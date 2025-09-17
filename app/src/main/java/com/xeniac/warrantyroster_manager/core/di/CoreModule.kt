package com.xeniac.warrantyroster_manager.core.di

import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.UserRepository
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.core.domain.use_cases.ForceLogoutUnauthorizedUserUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetIsUserLoggedInUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.GetUserProfileUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.LogoutUserUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.MainUseCases
import com.xeniac.warrantyroster_manager.core.domain.use_cases.StoreCurrentAppLocaleUseCase
import com.xeniac.warrantyroster_manager.core.domain.use_cases.UserUseCases
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.ConfirmPasswordChecker
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.PasswordStrengthCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object CoreModule {

    @Provides
    @ViewModelScoped
    fun providePasswordStrengthCalculator(): PasswordStrengthCalculator =
        PasswordStrengthCalculator()

    @Provides
    @ViewModelScoped
    fun provideConfirmPasswordChecker(): ConfirmPasswordChecker = ConfirmPasswordChecker()

    @Provides
    @ViewModelScoped
    fun provideGetCurrentAppLocaleUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): GetCurrentAppLocaleUseCase = GetCurrentAppLocaleUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideStoreCurrentAppLocaleUseCase(
        settingsDataStoreRepository: SettingsDataStoreRepository
    ): StoreCurrentAppLocaleUseCase = StoreCurrentAppLocaleUseCase(settingsDataStoreRepository)

    @Provides
    @ViewModelScoped
    fun provideGetIsUserLoggedInUseCase(
        firebaseAuth: FirebaseAuth,
        warrantyRosterDataStoreRepository: WarrantyRosterDataStoreRepository
    ): GetIsUserLoggedInUseCase = GetIsUserLoggedInUseCase(
        firebaseAuth,
        warrantyRosterDataStoreRepository
    )

    @Provides
    @ViewModelScoped
    fun provideGetUserProfileUseCase(
        userRepository: UserRepository
    ): GetUserProfileUseCase = GetUserProfileUseCase(userRepository)

    @Provides
    @ViewModelScoped
    fun provideLogoutUserUseCase(
        userRepository: UserRepository
    ): LogoutUserUseCase = LogoutUserUseCase(userRepository)

    @Provides
    @ViewModelScoped
    fun provideForceLogoutUnauthorizedUserUseCase(
        userRepository: UserRepository
    ): ForceLogoutUnauthorizedUserUseCase = ForceLogoutUnauthorizedUserUseCase(userRepository)

    @Provides
    @ViewModelScoped
    fun provideMainUseCases(
        getCurrentAppLocaleUseCase: GetCurrentAppLocaleUseCase,
        getIsUserLoggedInUseCase: GetIsUserLoggedInUseCase
    ): MainUseCases = MainUseCases(
        { getCurrentAppLocaleUseCase },
        { getIsUserLoggedInUseCase }
    )

    @Provides
    @ViewModelScoped
    fun provideUserUseCases(
        getUserProfileUseCase: GetUserProfileUseCase,
        logoutUserUseCase: LogoutUserUseCase,
        forceLogoutUnauthorizedUserUseCase: ForceLogoutUnauthorizedUserUseCase
    ): UserUseCases = UserUseCases(
        { getUserProfileUseCase },
        { logoutUserUseCase },
        { forceLogoutUnauthorizedUserUseCase }
    )
}