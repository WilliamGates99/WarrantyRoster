package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class CheckForAppUpdatesUseCase @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository
) {
    operator fun invoke(): Flow<AppUpdateInfo?> = appUpdateRepository.checkForAppUpdates()
}