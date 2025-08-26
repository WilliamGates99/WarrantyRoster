package com.xeniac.warrantyroster_manager.core.data.repositories

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import com.xeniac.warrantyroster_manager.core.domain.repositories.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectivityObserverImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : ConnectivityObserver {

    override fun observeNetworkConnection(): Flow<ConnectivityObserver.Status> = callbackFlow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val isConnectionValidated = networkCapabilities.hasCapability(
                        /* capability = */ NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    if (isConnectionValidated) {
                        launch { send(ConnectivityObserver.Status.VALIDATED) }
                    }
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.AVAILABLE) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.LOST) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.UNAVAILABLE) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        } else {
            send(ConnectivityObserver.Status.AVAILABLE)
            awaitClose { }
        }
    }.distinctUntilChanged()
}