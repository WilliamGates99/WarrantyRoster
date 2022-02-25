package com.xeniac.warrantyroster_manager.ui.main

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.shape.CornerFamily.ROUNDED
import com.google.android.material.shape.MaterialShapeDrawable
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityMainBinding
import com.xeniac.warrantyroster_manager.utils.Constants.DELETE_WARRANTY_Interstitial_ZONE_ID
import com.xeniac.warrantyroster_manager.utils.LocaleModifier
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

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

    fun requestInterstitialAd() = TapsellPlus.requestInterstitialAd(this,
        DELETE_WARRANTY_Interstitial_ZONE_ID, object : AdRequestCallback() {
            override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.response(tapsellPlusAdModel)
                Timber.i("RequestInterstitialAd Response: $tapsellPlusAdModel")
                tapsellPlusAdModel?.let { showInterstitialAd(it.responseId) }
            }

            override fun error(s: String?) {
                super.error(s)
                Timber.e("RequestInterstitialAd Error: $s")
            }
        })

    private fun showInterstitialAd(responseId: String) = TapsellPlus.showInterstitialAd(this,
        responseId, object : AdShowListener() {
            override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.onOpened(tapsellPlusAdModel)
            }

            override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.onClosed(tapsellPlusAdModel)
            }

            override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel?) {
                super.onError(tapsellPlusErrorModel)
            }
        })
}