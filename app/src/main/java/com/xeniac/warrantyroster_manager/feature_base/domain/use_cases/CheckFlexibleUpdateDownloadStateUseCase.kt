package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.IsUpdateDownloaded
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class CheckFlexibleUpdateDownloadStateUseCase @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository
) {
    operator fun invoke(): Flow<IsUpdateDownloaded> =
        appUpdateRepository.checkFlexibleUpdateDownloadState()
}