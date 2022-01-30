package com.xeniac.warrantyroster_manager.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_IS_LOGGED_IN_KEY
import com.xeniac.warrantyroster_manager.utils.Constants.PREFERENCE_LOGIN
import com.xeniac.warrantyroster_manager.utils.LocaleModifier

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    lateinit var viewModel: LandingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen()
    }

    private fun splashScreen() {
        installSplashScreen()

        val loginPrefs = getSharedPreferences(PREFERENCE_LOGIN, MODE_PRIVATE)
        val isLoggedIn = loginPrefs.getBoolean(PREFERENCE_IS_LOGGED_IN_KEY, false)

        if (isLoggedIn) {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        } else {
            landingInit()
        }
    }

    private fun landingInit() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = UserRepository()
        val viewModelProviderFactory = LandingViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[LandingViewModel::class.java]

        LocaleModifier.setLocale(this)
        setTitle()
    }

    private fun setTitle() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.fcv.id) as NavHostFragment

        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            destination.label?.let {
                when (it) {
                    "LoginFragment" -> {
                        binding.tvTitle.text = getString(R.string.login_text_title)
                    }
                    "RegisterFragment" -> {
                        binding.tvTitle.text = getString(R.string.register_text_title)
                    }
                    "ForgotPwFragment" -> {
                        binding.tvTitle.text = getString(R.string.forgot_pw_text_title)
                    }
                    "ForgotPwSentFragment" -> {
                        binding.tvTitle.text = getString(R.string.forgot_pw_sent_text_title)
                    }
                }
            }
        }
    }
}