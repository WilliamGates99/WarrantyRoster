package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.repositories.PermissionsDataStoreRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetNotificationPermissionCountUseCase @Inject constructor(
    private val permissionsDataStoreRepository: PermissionsDataStoreRepository
) {
    operator fun invoke(): Flow<Int> =
        permissionsDataStoreRepository.getNotificationPermissionCount()
}