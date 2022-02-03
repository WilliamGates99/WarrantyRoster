package com.xeniac.warrantyroster_manager.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.shape.CornerFamily.ROUNDED
import com.google.android.material.shape.MaterialShapeDrawable
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.ActivityMainBinding
import com.xeniac.warrantyroster_manager.db.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.repositories.UserRepository
import com.xeniac.warrantyroster_manager.repositories.WarrantyRepository
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModel
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.MainViewModelProviderFactory
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModelProviderFactory
import com.xeniac.warrantyroster_manager.utils.Constants.DELETE_WARRANTY_Interstitial_ZONE_ID
import com.xeniac.warrantyroster_manager.utils.LocaleModifier
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var viewModel: MainViewModel
    lateinit var settingsViewModel: SettingsViewModel

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainInit()
    }

    private fun mainInit() {
        mainViewModelSetup()
        settingsViewModelSetup()

        LocaleModifier.setLocale(this)
        bottomAppBarStyle()
        bottomNavActions()
        fabOnClick()
        seedCategories()
    }

    private fun mainViewModelSetup() {
        val warrantyRepository = WarrantyRepository(WarrantyRosterDatabase(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application, warrantyRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]
    }

    private fun settingsViewModelSetup() {
        val userRepository = UserRepository()
        val settingsViewModelProviderFactory =
            SettingsViewModelProviderFactory(application, userRepository)
        settingsViewModel = ViewModelProvider(
            this, settingsViewModelProviderFactory
        )[SettingsViewModel::class.java]
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
    }

    private fun fabOnClick() {
        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_mainActivity_to_addWarrantyFragment)
        }
    }

    fun showNavBar() {
        binding.appbar.performShow()
        binding.fab.show()
    }

    fun hideNavBar() {
        binding.fab.hide()
        binding.appbar.performHide()
    }

    private fun seedCategories() = viewModel.seedCategories()

    fun requestInterstitialAd() = TapsellPlus.requestInterstitialAd(this,
        DELETE_WARRANTY_Interstitial_ZONE_ID, object : AdRequestCallback() {
            override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.response(tapsellPlusAdModel)
                Log.i(TAG, "response: $tapsellPlusAdModel")
                tapsellPlusAdModel?.let { showInterstitialAd(it.responseId) }
            }

            override fun error(s: String?) {
                super.error(s)
                Log.e(TAG, "error: $s")
            }
        })

    private fun showInterstitialAd(responseId: String) = TapsellPlus.showInterstitialAd(this,
        responseId, object : AdShowListener() {
            override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.onOpened(tapsellPlusAdModel)
                Log.i(TAG, "onOpened: $tapsellPlusAdModel")
            }

            override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel?) {
                super.onClosed(tapsellPlusAdModel)
                Log.i(TAG, "onClosed: $tapsellPlusAdModel")
            }

            override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel?) {
                super.onError(tapsellPlusErrorModel)
                Log.e(TAG, "onError: $tapsellPlusErrorModel")
            }
        })
}