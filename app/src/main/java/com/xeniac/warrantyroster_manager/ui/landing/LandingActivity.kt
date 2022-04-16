package com.xeniac.warrantyroster_manager.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.ui.BaseActivity
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_PERSIAN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : BaseActivity() {

    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<SettingsViewModel>()

    private var shouldShowSplashScreen = true

    private lateinit var currentAppLanguage: String
    private lateinit var currentAppCountry: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen()
    }

    private fun splashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { shouldShowSplashScreen }

        if (viewModel.isUserLoggedIn()) {
            shouldShowSplashScreen = false
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        } else {
            shouldShowSplashScreen = false
            landingInit()
        }
    }

    private fun landingInit() {
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle()
        getCurrentAppLocale()
        currentAppLocaleObserver()
        languageOnClick()
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

    private fun getCurrentAppLocale() = viewModel.getCurrentAppLocale()

    private fun currentAppLocaleObserver() =
        viewModel.currentAppLocale.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { currentLocale ->
                currentAppLanguage = currentLocale[0]
                currentAppCountry = currentLocale[1]
                setCurrentLanguageFlag()
            }
        }

    private fun setCurrentLanguageFlag() {
        //TODO EDIT AFTER ADDING BRITISH ENGLISH
        when (currentAppLanguage) {
            LOCALE_LANGUAGE_ENGLISH -> binding.ivLanguageFlag.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.ic_flag_usa)
            )
            LOCALE_LANGUAGE_PERSIAN -> binding.ivLanguageFlag.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.ic_flag_iran)
            )
        }
    }

    private fun languageOnClick() = binding.clLanguage.setOnClickListener {
        val currentAppLanguageIndex = when (currentAppLanguage) {
            LOCALE_LANGUAGE_ENGLISH -> 0
            LOCALE_LANGUAGE_PERSIAN -> 1
            else -> 0
        }

        val languageItems = arrayOf(
            getString(R.string.landing_text_language_english),
            getString(R.string.landing_text_language_persian)
        )

        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.landing_text_language))
            setSingleChoiceItems(languageItems, currentAppLanguageIndex) { dialogInterface, index ->
                setAppLocale(index)
                dialogInterface.dismiss()
            }
        }.show()
    }

    private fun setAppLocale(index: Int) = viewModel.setAppLocale(index, this)
}