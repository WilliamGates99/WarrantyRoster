package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.CountDownTimerRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.TimerValueInSeconds
import kotlinx.coroutines.flow.Flow

class ObserveCountDownTimerUseCase(
    private val countDownTimerRepository: CountDownTimerRepository
) {
    operator fun invoke(): Flow<TimerValueInSeconds> =
        countDownTimerRepository.observeCountDownTimer()
}