package com.xeniac.warrantyroster_manager.feature_change_password.di

import com.xeniac.warrantyroster_manager.feature_change_password.domain.repositories.ChangeUserPasswordRepository
import com.xeniac.warrantyroster_manager.feature_change_password.domain.use_cases.ChangeUserPasswordUseCase
import com.xeniac.warrantyroster_manager.feature_change_password.domain.validation.ValidateConfirmNewPassword
import com.xeniac.warrantyroster_manager.feature_change_password.domain.validation.ValidateCurrentPassword
import com.xeniac.warrantyroster_manager.feature_change_password.domain.validation.ValidateNewPassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object ChangePasswordModule {

    @Provides
    @ViewModelScoped
    fun provideValidateCurrentPassword(): ValidateCurrentPassword = ValidateCurrentPassword()

    @Provides
    @ViewModelScoped
    fun provideValidateNewPassword(): ValidateNewPassword = ValidateNewPassword()

    @Provides
    @ViewModelScoped
    fun provideValidateConfirmNewPassword(): ValidateConfirmNewPassword =
        ValidateConfirmNewPassword()

    @Provides
    @ViewModelScoped
    fun provideChangeUserPasswordUseCase(
        changeUserPasswordRepository: ChangeUserPasswordRepository,
        validateCurrentPassword: ValidateCurrentPassword,
        validateNewPassword: ValidateNewPassword,
        validateConfirmNewPassword: ValidateConfirmNewPassword
    ): ChangeUserPasswordUseCase = ChangeUserPasswordUseCase(
        changeUserPasswordRepository,
        validateCurrentPassword,
        validateNewPassword,
        validateConfirmNewPassword
    )
}