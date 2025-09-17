package com.xeniac.warrantyroster_manager.core.domain.repositories

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observeNetworkConnection(): Flow<Status>

    enum class Status {
        VALIDATED,
        AVAILABLE,
        LOST,
        UNAVAILABLE
    }
}