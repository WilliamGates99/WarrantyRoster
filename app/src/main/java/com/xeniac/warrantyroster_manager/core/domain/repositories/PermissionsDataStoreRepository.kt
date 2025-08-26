package com.xeniac.warrantyroster_manager.core.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PermissionsDataStoreRepository {

    fun getNotificationPermissionCount(): Flow<Int>

    suspend fun storeNotificationPermissionCount(count: Int)
}