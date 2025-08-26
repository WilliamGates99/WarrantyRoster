package com.xeniac.warrantyroster_manager.core.presentation.main_activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.theme.utils.enableEdgeToEdgeWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen()
        enableEdgeToEdgeWindow()

        setContent {

        }
    }

    private fun splashScreen() {
        installSplashScreen().apply {
//            setKeepOnScreenCondition { viewModel.state.value.isSplashScreenLoading }
        }
    }
}