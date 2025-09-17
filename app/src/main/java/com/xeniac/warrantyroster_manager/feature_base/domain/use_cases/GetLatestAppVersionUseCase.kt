package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_base.domain.errors.GetLatestAppVersionError
import com.xeniac.warrantyroster_manager.feature_base.domain.models.LatestAppUpdateInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetLatestAppVersionUseCase(
    private val appUpdateRepository: AppUpdateRepository
) {
    operator fun invoke(): Flow<Result<LatestAppUpdateInfo?, GetLatestAppVersionError>> = flow {
        return@flow emit(appUpdateRepository.getLatestAppVersion())
    }
}