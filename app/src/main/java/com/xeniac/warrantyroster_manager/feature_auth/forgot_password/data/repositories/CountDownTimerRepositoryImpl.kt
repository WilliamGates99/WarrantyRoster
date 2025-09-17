package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.data.repositories

import android.os.CountDownTimer
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.CountDownTimerRepository
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.TimerValueInSeconds
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.utils.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CountDownTimerRepositoryImpl @Inject constructor() : CountDownTimerRepository {

    override fun observeCountDownTimer(): Flow<TimerValueInSeconds> = callbackFlow {
        val countDownTimer = object : CountDownTimer(
            /* millisInFuture = */  Constants.COUNT_DOWN_TIMER_DURATION_IN_MS,
            /* countDownInterval = */ Constants.COUNT_DOWN_TIMER_INTERVAL_IN_MS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                launch { send((millisUntilFinished / 1000).toInt()) }
            }

            override fun onFinish() {
                Timber.d("Count down timer is finished.")
                launch { send(0) }
                close()
            }
        }.start()

        awaitClose {
            countDownTimer?.cancel()
        }
    }
}