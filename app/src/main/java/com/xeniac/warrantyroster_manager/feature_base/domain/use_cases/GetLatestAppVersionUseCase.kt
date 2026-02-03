package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_base.domain.errors.GetLatestAppVersionError
import com.xeniac.warrantyroster_manager.feature_base.domain.models.LatestAppUpdateInfo
import com.xeniac.warrantyroster_manager.feature_base.domain.repositories.AppUpdateRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class GetLatestAppVersionUseCase @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository
) {
    operator fun invoke(): Flow<Result<LatestAppUpdateInfo?, GetLatestAppVersionError>> = flow {
        return@flow emit(appUpdateRepository.getLatestAppVersion())
    }
}