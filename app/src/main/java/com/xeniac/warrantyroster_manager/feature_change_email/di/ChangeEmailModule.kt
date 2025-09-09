package com.xeniac.warrantyroster_manager.feature_change_email.di

import com.xeniac.warrantyroster_manager.feature_change_email.domain.repositories.ChangeUserEmailRepository
import com.xeniac.warrantyroster_manager.feature_change_email.domain.use_cases.ChangeUserEmailUseCase
import com.xeniac.warrantyroster_manager.feature_change_email.domain.validation.ValidateNewEmail
import com.xeniac.warrantyroster_manager.feature_change_email.domain.validation.ValidatePassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object ChangeEmailModule {

    @Provides
    @ViewModelScoped
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @ViewModelScoped
    fun provideValidateNewEmail(): ValidateNewEmail = ValidateNewEmail()

    @Provides
    @ViewModelScoped
    fun provideChangeUserEmailUseCase(
        changeUserEmailRepository: ChangeUserEmailRepository,
        validatePassword: ValidatePassword,
        validateNewEmail: ValidateNewEmail
    ): ChangeUserEmailUseCase = ChangeUserEmailUseCase(
        changeUserEmailRepository,
        validatePassword,
        validateNewEmail
    )
}