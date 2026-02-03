package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.CountDownTimerRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.TimerValueInSeconds
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ObserveCountDownTimerUseCase @Inject constructor(
    private val countDownTimerRepository: CountDownTimerRepository
) {
    operator fun invoke(): Flow<TimerValueInSeconds> =
        countDownTimerRepository.observeCountDownTimer()
}