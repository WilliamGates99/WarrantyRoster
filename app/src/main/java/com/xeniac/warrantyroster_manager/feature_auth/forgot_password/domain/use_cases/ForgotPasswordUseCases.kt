package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases

import dagger.Lazy

data class ForgotPasswordUseCases(
    val observeCountDownTimerUseCase: Lazy<ObserveCountDownTimerUseCase>,
    val sendResetPasswordEmailUseCase: Lazy<SendResetPasswordEmailUseCase>
)