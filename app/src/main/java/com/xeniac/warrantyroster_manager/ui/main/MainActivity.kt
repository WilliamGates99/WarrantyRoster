package com.xeniac.warrantyroster_manager.ui.main

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.material.shape.CornerFamily.ROUNDED
import com.google.android.material.shape.MaterialShapeDrawable
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityMainBinding
import com.xeniac.warrantyroster_manager.utils.Constants.APPLOVIN_INTERSTITIAL_UNIT_ID
import com.xeniac.warrantyroster_manager.utils.Constants.TAPSELL_INTERSTITIAL_ZONE_ID
import com.xeniac.warrantyroster_manager.utils.LocaleModifier
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import timber.log.Timber

@Suppress("SpellCheckingInspection")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MaxAdListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    lateinit var appLovinAd: MaxInterstitialAd
    private var appLovinAdRequestCounter = 1

    var tapsellResponseId: String? = null
    private var tapsellRequestCounter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainInit()
    }

    private fun mainInit() {
        LocaleModifier.setLocale(this)
        bottomAppBarStyle()
        bottomNavActions()
        fabOnClick()
        requestAppLovinInterstitial()
    }

    private fun bottomAppBarStyle() {
        binding.bnv.background = null
        val radius = resources.getDimension(R.dimen.dimen_bottom_nav_radius)

        val shapeDrawable = binding.appbar.background as MaterialShapeDrawable
        shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel.toBuilder().apply {
            setTopRightCorner(ROUNDED, radius)
            setTopLeftCorner(ROUNDED, radius)
        }.build()
    }

    private fun bottomNavActions() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bnv, navController)
        binding.bnv.setOnItemReselectedListener { /* NO-OP */ }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.warrantiesFragment -> showNavBar()
                R.id.settingsFragment -> showNavBar()
                R.id.warrantyDetailsFragment -> hideNavBar()
                R.id.addWarrantyFragment -> hideNavBar()
                R.id.editWarrantyFragment -> hideNavBar()
                R.id.changeEmailFragment -> hideNavBar()
                R.id.changePasswordFragment -> hideNavBar()
                else -> showNavBar()
            }
        }
    }

    private fun fabOnClick() {
        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_mainActivity_to_addWarrantyFragment)
        }
    }

    private fun showNavBar() {
        binding.appbar.visibility = VISIBLE
        binding.appbar.performShow()
        binding.fab.show()
    }

    private fun hideNavBar() {
        binding.fab.hide()
        binding.appbar.performHide()
        binding.appbar.visibility = GONE
    }

    fun requestAppLovinInterstitial() {
        appLovinAd = MaxInterstitialAd(APPLOVIN_INTERSTITIAL_UNIT_ID, this).apply {
            setListener(this@MainActivity)
            loadAd()
        }
    }

    override fun onAdLoaded(ad: MaxAd?) {
        Timber.i("AppLovin onAdLoaded")
        appLovinAdRequestCounter = 0
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        Timber.e("AppLovin onAdLoadFailed: $error")
        if (appLovinAdRequestCounter < 2) {
            appLovinAdRequestCounter++
            appLovinAd.loadAd()
        } else {
            requestTapsellInterstitial()
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        Timber.e("AppLovin onAdDisplayFailed: $error")
        appLovinAd.loadAd()
    }

    override fun onAdHidden(ad: MaxAd?) {
        Timber.i("AppLovin onAdHidden")
        appLovinAd.loadAd()
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        Timber.i("AppLovin onAdDisplayed")
    }

    override fun onAdClicked(ad: MaxAd?) {
        Timber.i("AppLovin onAdClicked")
    }

    private fun requestTapsellInterstitial() {
        TapsellPlus.requestInterstitialAd(this,
            TAPSELL_INTERSTITIAL_ZONE_ID, object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.response(tapsellPlusAdModel)
                    Timber.i("requestTapsellInterstitial onResponse")
                    tapsellRequestCounter = 0
                    tapsellPlusAdModel?.let { tapsellResponseId = it.responseId }
                }

                override fun error(error: String?) {
                    super.error(error)
                    Timber.e("requestTapsellInterstitial onError: $error")
                    if (tapsellRequestCounter < 2) {
                        tapsellRequestCounter++
                        requestTapsellInterstitial()
                    }
                }
            })
    }
}