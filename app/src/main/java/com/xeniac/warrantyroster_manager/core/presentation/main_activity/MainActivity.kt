package com.xeniac.warrantyroster_manager.core.presentation.main_activity

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.nav_graphs.SetupRootNavGraph
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.WarrantyRosterTheme
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.utils.enableEdgeToEdgeWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen()
        enableEdgeToEdgeWindow()

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()

            // Layout Orientation is changing automatically in Android 7 (24) and newer
            when (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                true -> CompositionLocalProvider(
                    value = LocalLayoutDirection provides state.currentAppLocale.layoutDirectionCompose,
                    content = { WarrantyRosterRootSurface(postSplashDestination = state.postSplashDestination) }
                )
                else -> WarrantyRosterRootSurface(postSplashDestination = state.postSplashDestination)
            }
        }
    }

    private fun splashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.state.value.isSplashScreenLoading }
        }
    }

    @Composable
    fun WarrantyRosterRootSurface(
        postSplashDestination: Any?
    ) {
        WarrantyRosterTheme {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                val rootNavController = rememberNavController()

                postSplashDestination?.let { destination ->
                    SetupRootNavGraph(
                        rootNavController = rootNavController,
                        startDestination = destination
                    )
                }
            }
        }
    }
}