package com.xeniac.warrantyroster_manager.ui.landing

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.ui.BaseActivity
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.SettingsHelper
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : BaseActivity() {

    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<SettingsViewModel>()

    private var shouldShowSplashScreen = true

    private lateinit var currentAppLanguage: String
    private lateinit var currentAppCountry: String
    private lateinit var currentAppLocale: String

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

        subscribeToObservers()
        setTitle()
        getCurrentAppLocale()
        languageOnClick()
    }

    private fun subscribeToObservers() {
        currentAppLocaleObserver()
        setAppLocaleObserver()
    }

    private fun setTitle() {
        binding.apply {
            val navHostFragment = supportFragmentManager.findFragmentById(fcv.id) as NavHostFragment

            val navController = navHostFragment.navController

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.loginFragment -> {
                        tvTitle.text = getString(R.string.login_text_title)
                    }
                    R.id.registerFragment -> {
                        tvTitle.text = getString(R.string.register_text_title)
                    }
                    R.id.forgotPwFragment -> {
                        tvTitle.text = getString(R.string.forgot_pw_text_title)
                    }
                    R.id.forgotPwSentFragment -> {
                        tvTitle.text = getString(R.string.forgot_pw_sent_text_title)
                    }
                }
            }
        }
    }

    private fun getCurrentAppLocale() = viewModel.getCurrentAppLocale()

    private fun currentAppLocaleObserver() =
        viewModel.currentAppLocaleLiveData.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { currentLocale ->
                currentAppLanguage = currentLocale[0]
                currentAppCountry = currentLocale[1]
                currentAppLocale = "$currentAppLanguage-$currentAppCountry"
                setCurrentLanguageFlag()
            }
        }

    private fun setCurrentLanguageFlag() {
        binding.apply {
            when (currentAppLocale) {
                LOCALE_ENGLISH_UNITED_STATES -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(this@LandingActivity, R.drawable.ic_flag_usa)
                )
                LOCALE_ENGLISH_GREAT_BRITAIN -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(this@LandingActivity, R.drawable.ic_flag_gb)
                )
                LOCALE_PERSIAN_IRAN -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(this@LandingActivity, R.drawable.ic_flag_iran)
                )
            }
        }
    }

    private fun languageOnClick() = binding.clLanguage.setOnClickListener {
        val currentAppLocaleIndex = when (currentAppLocale) {
            LOCALE_ENGLISH_UNITED_STATES -> 0
            LOCALE_ENGLISH_GREAT_BRITAIN -> 1
            LOCALE_PERSIAN_IRAN -> 2
            else -> 0
        }

        val localeTextItems = arrayOf(
            getString(R.string.landing_text_language_english_us),
            getString(R.string.landing_text_language_english_gb),
            getString(R.string.landing_text_language_persian_ir)
        )

        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.landing_text_language))
            setSingleChoiceItems(localeTextItems, currentAppLocaleIndex) { dialogInterface, index ->
                setAppLocale(index)
                dialogInterface.dismiss()
            }
        }.show()
    }

    private fun setAppLocale(index: Int) = viewModel.setAppLocale(index)

    private fun setAppLocaleObserver() =
        viewModel.setAppLocaleLiveData.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.let {
                            val newAppLanguage = it[0]
                            val newAppCountry = it[1]

                            SettingsHelper.setAppLocale(
                                this,
                                newAppLanguage,
                                newAppCountry
                            )

                            startActivity(Intent(this, LandingActivity::class.java))
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        /* NO-OP */
                    }
                }
            }
        }
}