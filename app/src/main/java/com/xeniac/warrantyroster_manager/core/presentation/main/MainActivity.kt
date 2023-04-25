package com.xeniac.warrantyroster_manager.core.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.material.shape.CornerFamily.ROUNDED
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.landing.LandingActivity
import com.xeniac.warrantyroster_manager.databinding.ActivityMainBinding
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper.showThreeBtnAlertDialog
import com.xeniac.warrantyroster_manager.util.Constants.IN_APP_REVIEWS_DAYS_FROM_FIRST_INSTALL_TIME
import com.xeniac.warrantyroster_manager.util.Constants.IN_APP_REVIEWS_DAYS_FROM_PREVIOUS_REQUEST_TIME
import com.xeniac.warrantyroster_manager.util.DateHelper.getDaysFromFirstInstallTime
import com.xeniac.warrantyroster_manager.util.DateHelper.getDaysFromPreviousRequestTime
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MaxAdListener {

    private val viewModel by viewModels<MainActivityViewModel>()
    private var shouldShowSplashScreen = true

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var reviewManager: ReviewManager
    var reviewInfo: ReviewInfo? = null

    lateinit var appLovinAd: MaxInterstitialAd

    var tapsellResponseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen()
    }

    private fun splashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { shouldShowSplashScreen }

        if (viewModel.isUserLoggedIn()) {
            mainInit()
            shouldShowSplashScreen = false
        } else {
            shouldShowSplashScreen = false
            navigateToLandingActivity()
        }
    }

    private fun navigateToLandingActivity() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }

    private fun mainInit() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomAppBarStyle()
        bottomNavActions()
        fabOnClick()
        subscribeToObservers()
        getRateAppDialogChoice()
        requestAppLovinInterstitial()
    }

    private fun bottomAppBarStyle() {
        binding.bnv.background = null
        val radius = resources.getDimension(R.dimen.radius_20dp)

        val shapeDrawable = binding.appbar.background as MaterialShapeDrawable
        shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel.toBuilder().apply {
            setTopRightCorner(ROUNDED, radius)
            setTopLeftCorner(ROUNDED, radius)
        }.build()
    }

    private fun bottomNavActions() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.fcv.id) as NavHostFragment

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
                R.id.linkedAccountsFragment -> hideNavBar()
                R.id.changeEmailFragment -> hideNavBar()
                R.id.changePasswordFragment -> hideNavBar()
                else -> showNavBar()
            }
        }
    }

    private fun fabOnClick() = binding.fab.setOnClickListener {
        navController.navigate(R.id.action_mainActivity_to_addWarrantyFragment)
    }

    private fun showNavBar() = binding.apply {
        appbar.visibility = VISIBLE
        appbar.performShow()
        fab.show()
    }

    private fun hideNavBar() = binding.apply {
        fab.hide()
        appbar.performHide()
        appbar.visibility = GONE
    }

    private fun subscribeToObservers() {
        rateAppDialogChoiceObserver()
        previousRequestTimeInMillisObserver()
    }

    private fun getRateAppDialogChoice() = viewModel.getRateAppDialogChoice()

    private fun rateAppDialogChoiceObserver() =
        viewModel.rateAppDialogChoiceLiveData.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { rateAppDialogChoice ->
                when (rateAppDialogChoice) {
                    /**
                     * 0 -> Rate Now (Default)
                     * 1 -> Remind me later
                     * -1 -> No, Thanks (Never)
                     */
                    0 -> checkDaysFromFirstInstallTime()
                    1 -> getPreviousRequestTimeInMillis()
                    -1 -> {
                        /* NO-OP */
                    }
                }
            }
        }

    private fun checkDaysFromFirstInstallTime() {
        val daysFromFirstInstallTime = getDaysFromFirstInstallTime(this)
        Timber.i("It's been $daysFromFirstInstallTime days from first install time.")

        if (daysFromFirstInstallTime >= IN_APP_REVIEWS_DAYS_FROM_FIRST_INSTALL_TIME) {
            requestInAppReviews()
        }
    }

    private fun getPreviousRequestTimeInMillis() = viewModel.getPreviousRequestTimeInMillis()

    private fun previousRequestTimeInMillisObserver() =
        viewModel.previousRequestTimeInMillisLiveData.observe(this) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { previousRequestTimeInMillis ->
                checkDaysFromPreviousRequestTime(previousRequestTimeInMillis)
            }
        }

    private fun checkDaysFromPreviousRequestTime(previousRequestTimeInMillis: Long) {
        val daysFromPreviousRequestTime =
            getDaysFromPreviousRequestTime(previousRequestTimeInMillis)
        Timber.i("It's been $daysFromPreviousRequestTime days from the previous request time.")

        if (daysFromPreviousRequestTime >= IN_APP_REVIEWS_DAYS_FROM_PREVIOUS_REQUEST_TIME) {
            requestInAppReviews()
        }
    }

    private fun requestInAppReviews() {
        reviewManager = ReviewManagerFactory.create(this)
        val request = reviewManager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                reviewInfo = task.result
                Timber.i("InAppReviews request was successful.")
            } else {
                // There was some problem, log or handle the error code.
                reviewInfo = null
                Timber.e("InAppReviews request was not successful; Exception: ${task.exception}")
            }
        }
    }

    fun showRateAppDialog() = reviewInfo?.let {
        showThreeBtnAlertDialog(
            this,
            getString(R.string.main_rate_app_dialog_title, getString(R.string.app_name)),
            R.string.main_rate_app_dialog_message,
            R.string.main_rate_app_dialog_positive,
            R.string.main_rate_app_dialog_negative,
            R.string.main_rate_app_dialog_neutral,
            positiveAction = {
                showInAppReviews()
                setRateAppDialogChoiceToNever()
            },
            negativeAction = { setRateAppDialogChoiceToNever() },
            neutralAction = { setRateAppDialogChoiceToAskLater() }
        )
    }

    private fun setRateAppDialogChoiceToNever() = viewModel.setRateAppDialogChoice(-1)

    private fun setRateAppDialogChoiceToAskLater() {
        viewModel.setPreviousRequestTimeInMillis()
        viewModel.setRateAppDialogChoice(1)
    }

    private fun showInAppReviews() = reviewInfo?.let { reviewInfo ->
        val flow = reviewManager.launchReviewFlow(this, reviewInfo)

        flow.addOnCompleteListener {
            /**
             * The flow has finished. The API does not indicate whether the user
             * reviewed or not, or even whether the review dialog was shown. Thus, no
             * matter the result, we continue our app flow.
             */

            if (it.isSuccessful) {
                Timber.i("In-App Reviews Dialog was completed successfully.")
            } else {
                Timber.i("Something went wrong with showing the In-App Reviews Dialog; Exception: ${it.exception}")
            }
        }
    }

    fun requestAppLovinInterstitial() {
        appLovinAd = MaxInterstitialAd(BuildConfig.APPLOVIN_INTERSTITIAL_UNIT_ID, this).apply {
            setListener(this@MainActivity)
            loadAd()
        }
    }

    override fun onAdLoaded(ad: MaxAd?) {
        Timber.i("AppLovin onAdLoaded")
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        Timber.e("AppLovin onAdLoadFailed: $error")
        requestTapsellInterstitial()
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
            BuildConfig.TAPSELL_INTERSTITIAL_ZONE_ID, object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.response(tapsellPlusAdModel)
                    Timber.i("requestTapsellInterstitial onResponse")
                    tapsellPlusAdModel?.let { tapsellResponseId = it.responseId }
                }

                override fun error(error: String?) {
                    super.error(error)
                    Timber.e("requestTapsellInterstitial onError: $error")
                }
            })
    }
}