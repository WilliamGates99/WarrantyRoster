package com.xeniac.warrantyroster_manager.feature_auth.login.di

import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithFacebookUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.repositories.LoginWithEmailRepository
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases.LoginUseCases
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.use_cases.LoginWithEmailUseCase
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation.ValidateEmail
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.validation.ValidatePassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object LoginModule {

    @Provides
    @ViewModelScoped
    fun provideValidateEmail(): ValidateEmail = ValidateEmail()

    @Provides
    @ViewModelScoped
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @ViewModelScoped
    fun provideLoginWithEmailUseCase(
        loginWithEmailRepository: LoginWithEmailRepository,
        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword
    ): LoginWithEmailUseCase = LoginWithEmailUseCase(
        loginWithEmailRepository,
        validateEmail,
        validatePassword
    )

    @Provides
    @ViewModelScoped
    fun provideLoginUseCases(
        loginWithEmailUseCase: LoginWithEmailUseCase,
        getGoogleCredentialUseCase: GetGoogleCredentialUseCase,
        loginWithGoogleUseCase: LoginWithGoogleUseCase,
        checkPendingLoginWithXUseCase: CheckPendingLoginWithXUseCase,
        loginWithXUseCase: LoginWithXUseCase,
        loginWithFacebookUseCase: LoginWithFacebookUseCase
    ): LoginUseCases = LoginUseCases(
        { loginWithEmailUseCase },
        { getGoogleCredentialUseCase },
        { loginWithGoogleUseCase },
        { checkPendingLoginWithXUseCase },
        { loginWithXUseCase },
        { loginWithFacebookUseCase }
    )
}