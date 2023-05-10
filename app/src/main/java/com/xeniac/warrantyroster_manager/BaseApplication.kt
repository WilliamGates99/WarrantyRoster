package com.xeniac.warrantyroster_manager

import android.app.Application
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.xeniac.warrantyroster_manager.util.Constants.TAPSELL_KEY
import com.xeniac.warrantyroster_manager.util.SettingsHelper
import dagger.hilt.android.HiltAndroidApp
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @set:Inject
    var currentAppThemeIndex = 0

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setAppTheme()
        initFirebaseAppCheck()
        initAppLovin()
        initTapsell()
    }

    private fun setupTimber() = Timber.plant(Timber.DebugTree())

    private fun setAppTheme() = SettingsHelper.setAppTheme(currentAppThemeIndex)

    private fun initFirebaseAppCheck() {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }

    private fun initAppLovin() {
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk {}
        AppLovinPrivacySettings.setHasUserConsent(true, this)
    }

    private fun initTapsell() {
        TapsellPlus.initialize(this, TAPSELL_KEY, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                Timber.i("onInitializeSuccess: ${adNetworks?.name}")
            }

            override fun onInitializeFailed(adNetworks: AdNetworks?, error: AdNetworkError?) {
                Timber.e("onInitializeFailed: ${adNetworks?.name}, error: ${error?.errorMessage}")
            }
        })
        TapsellPlus.setGDPRConsent(this, true)
    }
}