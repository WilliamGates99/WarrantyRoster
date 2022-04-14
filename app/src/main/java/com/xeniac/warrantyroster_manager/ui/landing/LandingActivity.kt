package com.xeniac.warrantyroster_manager.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.ui.BaseActivity
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : BaseActivity() {

    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<SettingsViewModel>()

    private var shouldShowSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { shouldShowSplashScreen }

        getIsUserLoggedIn()
        isUserLoggedInObserver()
    }

    private fun getIsUserLoggedIn() = viewModel.isUserLoggedIn()

    private fun isUserLoggedInObserver() = viewModel.isUserLoggedIn.observe(this) { responseEvent ->
        responseEvent.getContentIfNotHandled()?.let { isUserLoggedIn ->
            shouldShowSplashScreen = false
            if (isUserLoggedIn) {
                Intent(this@LandingActivity, MainActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else {
                landingInit()
            }
        }
    }

    private fun landingInit() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle()
    }

    private fun setTitle() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.fcv.id) as NavHostFragment

        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                    binding.tvTitle.text = getString(R.string.login_text_title)
                }
                R.id.registerFragment -> {
                    binding.tvTitle.text = getString(R.string.register_text_title)
                }
                R.id.forgotPwFragment -> {
                    binding.tvTitle.text = getString(R.string.forgot_pw_text_title)
                }
                R.id.forgotPwSentFragment -> {
                    binding.tvTitle.text = getString(R.string.forgot_pw_sent_text_title)
                }
            }
        }
    }
}