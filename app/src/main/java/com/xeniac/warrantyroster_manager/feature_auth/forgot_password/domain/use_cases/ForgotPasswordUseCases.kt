package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases

import dagger.Lazy
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
data class ForgotPasswordUseCases @Inject constructor(
    val observeCountDownTimerUseCase: Lazy<ObserveCountDownTimerUseCase>,
    val sendResetPasswordEmailUseCase: Lazy<SendResetPasswordEmailUseCase>
)