package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import kotlinx.coroutines.flow.Flow

class CheckIsImmediateUpdateStalledUseCase(
    private val appUpdateRepository: AppUpdateRepository
) {
    operator fun invoke(): Flow<AppUpdateInfo?> =
        appUpdateRepository.checkIsImmediateUpdateStalled()
}