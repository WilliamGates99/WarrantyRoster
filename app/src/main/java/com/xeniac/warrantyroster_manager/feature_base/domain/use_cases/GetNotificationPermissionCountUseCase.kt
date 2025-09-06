package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.repositories.PermissionsDataStoreRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationPermissionCountUseCase(
    private val permissionsDataStoreRepository: PermissionsDataStoreRepository
) {
    operator fun invoke(): Flow<Int> =
        permissionsDataStoreRepository.getNotificationPermissionCount()
}