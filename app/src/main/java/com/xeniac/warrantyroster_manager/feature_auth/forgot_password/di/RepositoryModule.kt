package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.di

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.data.repositories.CountDownTimerRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.data.repositories.ForgotPasswordRepositoryImpl
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.CountDownTimerRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.ForgotPasswordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindForgotPasswordRepository(
        forgotPasswordRepositoryImpl: ForgotPasswordRepositoryImpl
    ): ForgotPasswordRepository

    @Binds
    @ViewModelScoped
    abstract fun bindCountDownTimerRepository(
        countDownTimerRepositoryImpl: CountDownTimerRepositoryImpl
    ): CountDownTimerRepository
}