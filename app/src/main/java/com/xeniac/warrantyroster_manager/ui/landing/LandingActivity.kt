package com.xeniac.warrantyroster_manager.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.utils.LocaleModifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<LandingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        getIsUserLoggedIn()
        isUserLoggedInObserver()
    }

    private fun getIsUserLoggedIn() = viewModel.isUserLoggedIn()

    private fun isUserLoggedInObserver() = viewModel.isUserLoggedIn.observe(this) { responseEvent ->
        responseEvent.getContentIfNotHandled()?.let { isUserLoggedIn ->
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

        LocaleModifier.setLocale(this)
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