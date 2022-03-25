package com.xeniac.warrantyroster_manager

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.adcolony.sdk.AdColony
import com.adcolony.sdk.AdColonyAppOptions
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.xeniac.warrantyroster_manager.di.CurrentCountry
import com.xeniac.warrantyroster_manager.di.CurrentLanguage
import com.xeniac.warrantyroster_manager.utils.Constants.ADCOLONY_APP_ID
import com.xeniac.warrantyroster_manager.utils.Constants.TAPSELL_KEY
import dagger.hilt.android.HiltAndroidApp
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @set:Inject
    var currentTheme = 0

    @Inject
    @CurrentLanguage
    lateinit var currentLanguage: String

    @Inject
    @CurrentCountry
    lateinit var currentCountry: String

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        initFirebaseAppCheck()
        setNightMode()
        setLocale()
        initAppLovin()
        initAdColony()
        initTapsell()
    }

    private fun setupTimber() = Timber.plant(Timber.DebugTree())

    private fun initFirebaseAppCheck() {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance())

        //TODO COMMENT FOR RELEASE
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
    }

    private fun setNightMode() {
        when (currentTheme) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun setLocale() {
        val resources = resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        val newLocale = Locale(currentLanguage, currentCountry)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, displayMetrics)
    }

    private fun initAppLovin() {
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk {}
        AppLovinPrivacySettings.setHasUserConsent(true, this)
    }

    private fun initAdColony() = AdColony.configure(
        this,
        AdColonyAppOptions().setKeepScreenOn(true),
        ADCOLONY_APP_ID
    )

    @Suppress("SpellCheckingInspection")
    private fun initTapsell() = TapsellPlus.initialize(
        this, TAPSELL_KEY, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                Timber.i("onInitializeSuccess: ${adNetworks?.name}")
            }

            override fun onInitializeFailed(adNetworks: AdNetworks?, error: AdNetworkError?) {
                Timber.e("onInitializeFailed: ${adNetworks?.name}, error: ${error?.errorMessage}")
            }
        })
}