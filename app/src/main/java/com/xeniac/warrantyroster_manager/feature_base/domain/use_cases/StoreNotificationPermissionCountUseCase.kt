package com.xeniac.warrantyroster_manager.feature_base.domain.use_cases

import com.xeniac.warrantyroster_manager.core.domain.repositories.PermissionsDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreNotificationPermissionCountUseCase(
    private val permissionsDataStoreRepository: PermissionsDataStoreRepository
) {
    operator fun invoke(
        count: Int
    ): Flow<Unit> = flow {
        return@flow emit(permissionsDataStoreRepository.storeNotificationPermissionCount(count))
    }
}