package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories

import kotlinx.coroutines.flow.Flow

typealias TimerValueInSeconds = Int

interface CountDownTimerRepository {
    fun observeCountDownTimer(): Flow<TimerValueInSeconds>
}