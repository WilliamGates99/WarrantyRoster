package com.xeniac.warrantyroster_manager.core.data.repository

import com.xeniac.warrantyroster_manager.core.domain.repository.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class FakeNetworkConnectivityObserver : ConnectivityObserver {

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            launch {
                if (shouldReturnNetworkError) {
                    send(ConnectivityObserver.Status.UNAVAILABLE)
                } else {
                    send(ConnectivityObserver.Status.AVAILABLE)
                }
            }
        }.distinctUntilChanged()
    }
}