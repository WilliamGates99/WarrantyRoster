package com.xeniac.warrantyroster_manager.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.xeniac.warrantyroster_manager.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    private lateinit var connectivityObserver: ConnectivityObserver
    var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.AVAILABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectivityObserver = NetworkConnectivityObserver(this)

        networkConnectivityObserver()
    }

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        }
    }
}