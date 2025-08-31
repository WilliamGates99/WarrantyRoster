package com.xeniac.warrantyroster_manager.feature_auth.login.di

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
        loginWithEmailUseCase: LoginWithEmailUseCase
    ): LoginUseCases = LoginUseCases(
        { loginWithEmailUseCase }
    )
}