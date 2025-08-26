package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.core.domain.repositories.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object NetworkObserverHelper {

    private val networkStatus = MutableStateFlow<ConnectivityObserver.Status?>(null)

    fun observeNetworkConnection(
        connectivityObserver: ConnectivityObserver
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            connectivityObserver.observeNetworkConnection().onEach { newStatus ->
                networkStatus.update { newStatus }
            }.launchIn(scope = this)
        }
    }

    fun hasNetworkConnection(): Boolean = when (networkStatus.value) {
        ConnectivityObserver.Status.VALIDATED -> true
        ConnectivityObserver.Status.AVAILABLE -> true
        else -> false
    }
}