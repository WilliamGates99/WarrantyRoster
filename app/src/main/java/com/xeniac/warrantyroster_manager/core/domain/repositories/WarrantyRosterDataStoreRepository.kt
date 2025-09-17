package com.xeniac.warrantyroster_manager.core.domain.repositories

interface WarrantyRosterDataStoreRepository {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun isUserLoggedIn(isLoggedIn: Boolean)
}