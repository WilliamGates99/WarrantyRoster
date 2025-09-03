package com.xeniac.warrantyroster_manager.feature_auth.register.di

import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.CheckPendingLoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.GetGoogleCredentialUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithFacebookUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithGoogleUseCase
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.use_cases.LoginWithXUseCase
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.repositories.RegisterRepository
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases.RegisterUseCases
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.use_cases.RegisterWithEmailUseCase
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation.ValidateConfirmPassword
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation.ValidateEmail
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.validation.ValidatePassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object RegisterModule {

    @Provides
    @ViewModelScoped
    fun provideValidateEmail(): ValidateEmail = ValidateEmail()

    @Provides
    @ViewModelScoped
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @ViewModelScoped
    fun provideValidateConfirmPassword(): ValidateConfirmPassword = ValidateConfirmPassword()

    @Provides
    @ViewModelScoped
    fun provideRegisterWithEmailUseCase(
        registerRepository: RegisterRepository,
        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword,
        validateConfirmPassword: ValidateConfirmPassword
    ): RegisterWithEmailUseCase = RegisterWithEmailUseCase(
        registerRepository,
        validateEmail,
        validatePassword,
        validateConfirmPassword
    )

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCases(
        registerWithEmailUseCase: RegisterWithEmailUseCase,
        getGoogleCredentialUseCase: GetGoogleCredentialUseCase,
        loginWithGoogleUseCase: LoginWithGoogleUseCase,
        checkPendingLoginWithXUseCase: CheckPendingLoginWithXUseCase,
        loginWithXUseCase: LoginWithXUseCase,
        loginWithFacebookUseCase: LoginWithFacebookUseCase
    ): RegisterUseCases = RegisterUseCases(
        { registerWithEmailUseCase },
        { getGoogleCredentialUseCase },
        { loginWithGoogleUseCase },
        { checkPendingLoginWithXUseCase },
        { loginWithXUseCase },
        { loginWithFacebookUseCase }
    )
}