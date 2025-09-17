package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.di

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.CountDownTimerRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.ForgotPasswordRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases.ForgotPasswordUseCases
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases.ObserveCountDownTimerUseCase
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases.SendResetPasswordEmailUseCase
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.validation.ValidateEmail
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.validation.ValidateTimerValue
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object ForgotPasswordModule {

    @Provides
    @ViewModelScoped
    fun provideValidateEmail(): ValidateEmail = ValidateEmail()

    @Provides
    @ViewModelScoped
    fun provideValidateTimerValue(): ValidateTimerValue = ValidateTimerValue()

    @Provides
    @ViewModelScoped
    fun provideObserveCountDownTimerUseCase(
        countDownTimerRepository: CountDownTimerRepository
    ): ObserveCountDownTimerUseCase = ObserveCountDownTimerUseCase(countDownTimerRepository)

    @Provides
    @ViewModelScoped
    fun provideSendResetPasswordEmailUseCase(
        forgotPasswordRepository: ForgotPasswordRepository,
        validateEmail: ValidateEmail,
        validateTimerValue: ValidateTimerValue
    ): SendResetPasswordEmailUseCase = SendResetPasswordEmailUseCase(
        forgotPasswordRepository,
        validateEmail,
        validateTimerValue
    )

    @Provides
    @ViewModelScoped
    fun provideForgotPasswordUseCases(
        observeCountDownTimerUseCase: ObserveCountDownTimerUseCase,
        sendResetPasswordEmailUseCase: SendResetPasswordEmailUseCase
    ): ForgotPasswordUseCases = ForgotPasswordUseCases(
        { observeCountDownTimerUseCase },
        { sendResetPasswordEmailUseCase }
    )
}