package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.IsUpdateDownloaded
import kotlinx.coroutines.flow.Flow

class CheckIsFlexibleUpdateStalledUseCase(
    private val appUpdateRepository: AppUpdateRepository
) {
    operator fun invoke(): Flow<IsUpdateDownloaded> =
        appUpdateRepository.checkIsFlexibleUpdateStalled()
}