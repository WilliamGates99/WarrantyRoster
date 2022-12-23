package com.xeniac.warrantyroster_manager.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.ui.LandingActivity
import com.xeniac.warrantyroster_manager.ui.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.AlertDialogHelper.showOneBtnAlertDialog
import com.xeniac.warrantyroster_manager.utils.AlertDialogHelper.showSingleChoiceItemsDialog
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_EMAIL_VERIFICATION_EMAIL_NOT_PROVIDED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.THEME_INDEX_DARK
import com.xeniac.warrantyroster_manager.utils.Constants.THEME_INDEX_DEFAULT
import com.xeniac.warrantyroster_manager.utils.Constants.THEME_INDEX_LIGHT
import com.xeniac.warrantyroster_manager.utils.Constants.URL_CROWDIN
import com.xeniac.warrantyroster_manager.utils.Constants.URL_DONATE
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.utils.LinkHelper.openLink
import com.xeniac.warrantyroster_manager.utils.LinkHelper.openPlayStore
import com.xeniac.warrantyroster_manager.utils.Resource
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNormalSnackbarError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), MaxAdRevenueListener {

    private var _binding: FragmentSettingsBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: SettingsViewModel

    private var currentLocaleIndex = 0
    private var currentAppTheme = 0

    private lateinit var appLovinNativeAdContainer: ViewGroup
    private lateinit var appLovinAdLoader: MaxNativeAdLoader
    private var appLovinNativeAd: MaxAd? = null

    private var tapsellResponseId: String? = null

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        subscribeToObservers()
        getAccountDetails()
        getCurrentLanguage()
        getCurrentAppTheme()
        verifyOnClick()
        linkedAccountsOnClick()
        changeEmailOnClick()
        changePasswordOnClick()
        languageOnClick()
        themeOnClick()
        donateOnClick()
        improveTranslationsOnClick()
        rateUsOnClick()
        privacyPolicyOnClick()
        logoutOnClick()
        requestAppLovinNativeAd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        destroyAd()
        _binding = null
    }

    private fun subscribeToObservers() {
        cachedAccountDetailsObserver()
        reloadedAccountDetailsObserver()
        currentLanguageObserver()
        currentLocaleIndexObserver()
        currentAppThemeObserver()
        changeCurrentLocaleObserver()
        sendVerificationEmailObserver()
        logoutObserver()
    }

    private fun getAccountDetails() {
        getCachedAccountDetails()

        if ((requireActivity() as MainActivity).networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            getReloadedAccountDetails()
        }
    }

    private fun getCachedAccountDetails() = viewModel.getCachedAccountDetails()

    private fun cachedAccountDetailsObserver() =
        viewModel.cachedAccountDetailsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        /* NO-OP */
                    }
                    is Resource.Success -> {
                        response.data?.let { user ->
                            setAccountDetails(user.email.toString(), user.isEmailVerified)
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
            }
        }

    private fun getReloadedAccountDetails() = viewModel.getReloadedAccountDetails()

    private fun reloadedAccountDetailsObserver() =
        viewModel.reloadedAccountDetailsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        /* NO-OP */
                    }
                    is Resource.Success -> {
                        response.data?.let { user ->
                            setAccountDetails(user.email.toString(), user.isEmailVerified)
                        }
                    }
                    is Resource.Error -> {
                        getCachedAccountDetails()
                    }
                }
            }
        }

    private fun setAccountDetails(email: String, isEmailVerified: Boolean) {
        binding.apply {
            userEmail = email
            isUserVerified = isEmailVerified

            if (isEmailVerified) {
                lavAccountVerification.speed = 0.60f
                lavAccountVerification.repeatCount = 0
                lavAccountVerification.setAnimation(R.raw.anim_account_verified)
            } else {
                lavAccountVerification.speed = 1.00f
                lavAccountVerification.repeatCount = LottieDrawable.INFINITE
                lavAccountVerification.setAnimation(R.raw.anim_account_not_verified)
            }
            lavAccountVerification.playAnimation()
        }
    }

    private fun getCurrentLanguage() = viewModel.getCurrentLanguage()

    private fun currentLanguageObserver() =
        viewModel.currentLanguageLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { currentLanguage ->
                binding.currentLanguage = currentLanguage.asString(requireContext())
            }
        }

    private fun currentLocaleIndexObserver() =
        viewModel.currentLocaleIndexLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { index ->
                currentLocaleIndex = index
            }
        }

    private fun getCurrentAppTheme() = viewModel.getCurrentAppTheme()

    private fun currentAppThemeObserver() =
        viewModel.currentAppThemeLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { currentThemeIndex ->
                currentAppTheme = currentThemeIndex
                setCurrentThemeText()
            }
        }

    private fun setCurrentThemeText() {
        requireContext().apply {
            binding.currentTheme = when (currentAppTheme) {
                THEME_INDEX_DEFAULT -> getString(R.string.settings_text_settings_theme_default)
                THEME_INDEX_LIGHT -> getString(R.string.settings_text_settings_theme_light)
                THEME_INDEX_DARK -> getString(R.string.settings_text_settings_theme_dark)
                else -> getString(R.string.settings_text_settings_theme_default)
            }
        }
    }

    private fun verifyOnClick() = binding.btnAccountVerification.setOnClickListener {
        sendVerificationEmail()
    }

    private fun sendVerificationEmail() {
        if ((requireActivity() as MainActivity).networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel.sendVerificationEmail()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { sendVerificationEmail() }
            Timber.e("sendVerificationEmail Error: Offline")
        }
    }

    private fun sendVerificationEmailObserver() =
        viewModel.sendVerificationEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        showOneBtnAlertDialog(
                            requireContext(),
                            R.string.settings_dialog_message,
                            R.string.settings_dialog_positive
                        )
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.asString(requireContext())?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkFailureError(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), requireView())
                                }
                                it.contains(
                                    ERROR_FIREBASE_AUTH_EMAIL_VERIFICATION_EMAIL_NOT_PROVIDED
                                ) -> {
                                    showNormalSnackbarError(
                                        requireView(),
                                        requireContext().getString(R.string.settings_error_email_verification_email_not_provided)
                                    )
                                }
                                else -> showSomethingWentWrongError(requireContext(), requireView())
                            }
                        }
                    }
                }
            }
        }

    private fun linkedAccountsOnClick() = binding.clAccountLinkedAccounts.setOnClickListener {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToLinkedAccountsFragment())
    }

    private fun changeEmailOnClick() = binding.clAccountChangeEmail.setOnClickListener {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment())
    }

    private fun changePasswordOnClick() = binding.clAccountChangePassword.setOnClickListener {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment())
    }

    private fun languageOnClick() = binding.clSettingsLanguage.setOnClickListener {
        val localeTextItems = arrayOf(
            requireContext().getString(R.string.settings_dialog_item_language_english_us),
            requireContext().getString(R.string.settings_dialog_item_language_english_gb),
            requireContext().getString(R.string.settings_dialog_item_language_persian_ir)
        )

        showSingleChoiceItemsDialog(
            requireContext(),
            R.string.settings_dialog_title_language,
            localeTextItems,
            currentLocaleIndex
        ) { index ->
            changeCurrentLocale(index)
        }
    }

    private fun changeCurrentLocale(index: Int) = viewModel.changeCurrentLocale(index)

    private fun changeCurrentLocaleObserver() =
        viewModel.changeCurrentLocaleLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { isActivityRestartNeeded ->
                if (isActivityRestartNeeded) {
                    requireActivity().apply {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    viewModel.getCurrentLanguage()
                }
            }
        }

    private fun themeOnClick() = binding.clSettingsTheme.setOnClickListener {
        val themeItems = arrayOf(
            requireContext().getString(R.string.settings_dialog_item_theme_default),
            requireContext().getString(R.string.settings_dialog_item_theme_light),
            requireContext().getString(R.string.settings_dialog_item_theme_dark)
        )

        showSingleChoiceItemsDialog(
            requireContext(),
            R.string.settings_dialog_title_theme,
            themeItems,
            currentAppTheme
        ) { index ->
            changeCurrentTheme(index)
        }
    }

    private fun changeCurrentTheme(index: Int) = viewModel.changeCurrentTheme(index)

    private fun donateOnClick() = binding.clSettingsDonate.setOnClickListener {
        openLink(requireContext(), requireView(), URL_DONATE)
    }

    private fun improveTranslationsOnClick() =
        binding.clSettingsImproveTranslations.setOnClickListener {
            openLink(requireContext(), requireView(), URL_CROWDIN)
        }

    private fun rateUsOnClick() = binding.clSettingsRateUs.setOnClickListener {
        openPlayStore(requireContext(), requireView())
    }

    private fun privacyPolicyOnClick() = binding.clSettingsPrivacyPolicy.setOnClickListener {
        openLink(requireContext(), requireView(), URL_PRIVACY_POLICY)
    }

    private fun logoutOnClick() = binding.btnLogout.setOnClickListener {
        logoutUser()
    }

    private fun logoutUser() = viewModel.logoutUser()

    private fun logoutObserver() =
        viewModel.logoutLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        /* NO-OP */
                    }
                    is Resource.Success -> {
                        requireActivity().apply {
                            startActivity(Intent(this, LandingActivity::class.java))
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        btnAccountVerification.visibility = GONE
        cpiVerify.visibility = VISIBLE

    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiVerify.visibility = GONE
        btnAccountVerification.visibility = VISIBLE
    }

    private fun requestAppLovinNativeAd() {
        appLovinNativeAdContainer = binding.flAdContainerNative
        appLovinAdLoader =
            MaxNativeAdLoader(
                BuildConfig.APPLOVIN_SETTINGS_NATIVE_UNIT_ID,
                requireContext()
            ).apply {
                setRevenueListener(this@SettingsFragment)
                setNativeAdListener(AppLovinNativeAdListener())
                loadAd(createNativeAdView())
            }
    }

    private fun createNativeAdView(): MaxNativeAdView {
        val nativeAdBinder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.ad_banner_settings_applovin).apply {
                setIconImageViewId(R.id.iv_banner_icon)
                setTitleTextViewId(R.id.tv_banner_title)
                setBodyTextViewId(R.id.tv_banner_body)
                setCallToActionButtonId(R.id.btn_banner_action)
            }.build()
        return MaxNativeAdView(nativeAdBinder, requireContext())
    }

    private inner class AppLovinNativeAdListener : MaxNativeAdListener() {
        override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd?) {
            super.onNativeAdLoaded(nativeAdView, nativeAd)
            Timber.i("AppLovin onNativeAdLoaded")

            appLovinNativeAd?.let {
                // Clean up any pre-existing native ad to prevent memory leaks.
                appLovinAdLoader.destroy(it)
            }

            showNativeAdContainer()
            appLovinNativeAd = nativeAd
            appLovinNativeAdContainer.removeAllViews()
            appLovinNativeAdContainer.addView(nativeAdView)
        }

        override fun onNativeAdLoadFailed(adUnitId: String?, error: MaxError?) {
            super.onNativeAdLoadFailed(adUnitId, error)
            Timber.e("AppLovin onNativeAdLoadFailed: ${error?.message}")
            initTapsellAdHolder()
        }

        override fun onNativeAdClicked(nativeAd: MaxAd?) {
            super.onNativeAdClicked(nativeAd)
            Timber.i("AppLovin onNativeAdClicked")
        }
    }

    override fun onAdRevenuePaid(ad: MaxAd?) {
        Timber.i("AppLovin onAdRevenuePaid")
    }

    private fun initTapsellAdHolder() {
        _binding?.let {
            val adHolder = TapsellPlus.createAdHolder(
                requireActivity(), binding.flAdContainerNative, R.layout.ad_banner_settings_tapsell
            )
            adHolder?.let { requestTapsellNativeAd(it) }
        }
    }

    private fun requestTapsellNativeAd(adHolder: AdHolder) {
        _binding?.let {
            TapsellPlus.requestNativeAd(requireActivity(),
                BuildConfig.TAPSELL_SETTINGS_NATIVE_ZONE_ID, object : AdRequestCallback() {
                    override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                        super.response(tapsellPlusAdModel)
                        Timber.i("requestTapsellNativeAd onResponse")
                        _binding?.let {
                            tapsellPlusAdModel?.let {
                                tapsellResponseId = it.responseId
                                showNativeAd(adHolder, tapsellResponseId!!)
                            }
                        }
                    }

                    override fun error(error: String?) {
                        super.error(error)
                        Timber.e("requestTapsellNativeAd onError: $error")
                        requestTapsellNativeAd(adHolder)
                    }
                })
        }
    }

    private fun showNativeAd(adHolder: AdHolder, responseId: String) {
        _binding?.let {
            showNativeAdContainer()
            TapsellPlus.showNativeAd(requireActivity(),
                responseId, adHolder, object : AdShowListener() {
                    override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel?) {
                        super.onOpened(tapsellPlusAdModel)
                    }

                    override fun onClosed(tapsellPlusAdModel: TapsellPlusAdModel?) {
                        super.onClosed(tapsellPlusAdModel)
                    }
                })
        }
    }

    private fun showNativeAdContainer() = binding.apply {
        flAdContainerNative.visibility = VISIBLE
        dividerSettingsAdContainer.visibility = VISIBLE
    }

    private fun destroyAd() {
        appLovinNativeAd?.let {
            appLovinAdLoader.destroy(it)
        }

        tapsellResponseId?.let {
            TapsellPlus.destroyNativeBanner(requireActivity(), it)
        }
    }
}