package com.xeniac.warrantyroster_manager.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_PERSIAN_IRAN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    private val viewModel by viewModels<LandingViewModel>()

    private var currentLocaleIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()
        getCurrentLanguage()
        setTitleText()
        languageOnClick()
    }

    private fun subscribeToObservers() {
        currentAppLocaleObserver()
        changeCurrentLocaleObserver()
    }

    private fun getCurrentLanguage() = viewModel.getCurrentLanguage()

    private fun currentAppLocaleObserver() =
        viewModel.currentLocaleIndexLiveData.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { index ->
                currentLocaleIndex = index
                setCurrentLanguageFlag(index)
            }
        }

    private fun setCurrentLanguageFlag(index: Int) {
        binding.apply {
            when (index) {
                LOCALE_INDEX_ENGLISH_UNITED_STATES -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(this@LandingActivity, R.drawable.ic_flag_usa)
                )
                LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(this@LandingActivity, R.drawable.ic_flag_gb)
                )
                LOCALE_INDEX_PERSIAN_IRAN -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(this@LandingActivity, R.drawable.ic_flag_iran)
                )
            }
        }
    }

    private fun setTitleText() = binding.apply {
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

    private fun languageOnClick() = binding.clLanguage.setOnClickListener {
        val localeTextItems = arrayOf(
            getString(R.string.landing_dialog_item_language_english_us),
            getString(R.string.landing_dialog_item_language_english_gb),
            getString(R.string.landing_dialog_item_language_persian_ir)
        )

        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.landing_dialog_title_language))
            setSingleChoiceItems(localeTextItems, currentLocaleIndex) { dialogInterface, index ->
                changeCurrentLocale(index)
                dialogInterface.dismiss()
            }
        }.show()
    }

    private fun changeCurrentLocale(index: Int) = viewModel.changeCurrentLocale(index)

    private fun changeCurrentLocaleObserver() =
        viewModel.changeCurrentLocaleLiveData.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { isActivityRestartNeeded ->
                if (isActivityRestartNeeded) {
                    startActivity(Intent(this, LandingActivity::class.java))
                    finish()
                } else {
                    viewModel.getCurrentLanguage()
                }
            }
        }
}