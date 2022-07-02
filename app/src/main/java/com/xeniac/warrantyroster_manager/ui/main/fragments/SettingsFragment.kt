package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.ui.landing.LandingActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_ENGLISH
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_LANGUAGE_PERSIAN
import com.xeniac.warrantyroster_manager.utils.Constants.URL_DONATE
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.utils.LinkHelper.openLink
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.*
import ir.tapsell.plus.model.TapsellPlusAdModel
import timber.log.Timber

@Suppress("SpellCheckingInspection")
@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), MaxAdRevenueListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var currentAppLanguage: String
    private lateinit var currentAppCountry: String
    private var currentAppTheme = 0

    private lateinit var appLovinNativeAdContainer: ViewGroup
    private lateinit var appLovinAdLoader: MaxNativeAdLoader
    private var appLovinNativeAd: MaxAd? = null
    private var appLovinAdRequestCounter = 1

    private var tapsellResponseId: String? = null
    private var tapsellRequestCounter = 1

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        subscribeToObservers()
        getAccountDetails()
        getCurrentAppLocale()
        getCurrentApptheme()
        verifyOnClick()
        changeEmailOnClick()
        changePasswordOnClick()
        languageOnClick()
        themeOnClick()
        donateOnClick()
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
        accountDetailsObserver()
        currentAppLocaleObserver()
        currentAppThemeObserver()
        sendVerificationEmailObserver()
        logoutObserver()
    }

    private fun getAccountDetails() = viewModel.getAccountDetails()

    private fun accountDetailsObserver() =
        viewModel.accountDetailsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.let { user ->
                            setAccountDetails(user.email.toString(), user.isEmailVerified)
                        }
                    }
                    Status.ERROR -> {
                        /* NO-OP */
                    }
                    Status.LOADING -> {
                        /* NO-OP */
                    }
                }
            }
        }

    private fun setAccountDetails(email: String, isEmailVerified: Boolean) {
        binding.tvAccountEmail.text = email

        if (isEmailVerified) {
            binding.btnAccountVerification.isClickable = false
            binding.btnAccountVerification.text =
                requireContext().getString(R.string.settings_btn_account_verified)
            binding.btnAccountVerification.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
            binding.btnAccountVerification.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green20)
            binding.ivAccountEmail.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.green10)
            )
            binding.lavAccountVerification.repeatCount = 0
            binding.lavAccountVerification.speed = 0.60f
            binding.lavAccountVerification.setAnimation(R.raw.anim_account_verified)
        } else {
            binding.btnAccountVerification.isClickable = true
            binding.btnAccountVerification.text =
                requireContext().getString(R.string.settings_btn_account_verify)
            binding.btnAccountVerification.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.blue)
            )
            binding.btnAccountVerification.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.blue20)
            binding.ivAccountEmail.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.red10)
            )
            binding.lavAccountVerification.repeatCount = LottieDrawable.INFINITE
            binding.lavAccountVerification.speed = 1.00f
            binding.lavAccountVerification.setAnimation(R.raw.anim_account_not_verified)
        }
        binding.lavAccountVerification.playAnimation()
    }

    private fun getCurrentAppLocale() = viewModel.getCurrentAppLocale()

    private fun currentAppLocaleObserver() =
        viewModel.currentAppLocale.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { currentLocale ->
                currentAppLanguage = currentLocale[0]
                currentAppCountry = currentLocale[1]
                setCurrentLanguageText()
            }
        }

    private fun setCurrentLanguageText() {
        //TODO EDIT AFTER ADDING BRITISH ENGLISH
        when (currentAppLanguage) {
            LOCALE_LANGUAGE_ENGLISH -> {
                binding.tvSettingsLanguageCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_language_english)
            }
            LOCALE_LANGUAGE_PERSIAN -> {
                binding.tvSettingsLanguageCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_language_persian)
            }
        }
    }

    private fun getCurrentApptheme() = viewModel.getCurrentAppTheme()

    private fun currentAppThemeObserver() =
        viewModel.currentAppTheme.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { currentThemeIndex ->
                currentAppTheme = currentThemeIndex
                setCurrentThemeText()
            }
        }

    private fun setCurrentThemeText() {
        when (currentAppTheme) {
            0 -> {
                binding.tvSettingsThemeCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_theme_default)
            }
            1 -> {
                binding.tvSettingsThemeCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_theme_light)
            }
            2 -> {
                binding.tvSettingsThemeCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_theme_dark)
            }
        }
    }

    private fun verifyOnClick() = binding.btnAccountVerification.setOnClickListener {
        sendVerificationEmail()
    }

    private fun changeEmailOnClick() = binding.clAccountChangeEmail.setOnClickListener {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment())
    }

    private fun changePasswordOnClick() = binding.clAccountChangePassword.setOnClickListener {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment())
    }

    private fun languageOnClick() = binding.clSettingsLanguage.setOnClickListener {
        //TODO EDIT AFTER ADDING BRITISH ENGLISH
        val currentAppLanguageIndex = when (currentAppLanguage) {
            LOCALE_LANGUAGE_ENGLISH -> 0
            LOCALE_LANGUAGE_PERSIAN -> 1
            else -> 0
        }

        val languageItems = arrayOf(
            requireContext().getString(R.string.settings_text_settings_language_english),
            requireContext().getString(R.string.settings_text_settings_language_persian)
        )

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(requireContext().getString(R.string.settings_text_settings_language))
            setSingleChoiceItems(languageItems, currentAppLanguageIndex) { dialogInterface, index ->
                setAppLocale(index)
                dialogInterface.dismiss()
            }
        }.show()
    }

    private fun themeOnClick() = binding.clSettingsTheme.setOnClickListener {
        val themeItems = arrayOf(
            requireContext().getString(R.string.settings_text_settings_theme_default),
            requireContext().getString(R.string.settings_text_settings_theme_light),
            requireContext().getString(R.string.settings_text_settings_theme_dark)
        )

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(requireContext().getString(R.string.settings_text_settings_theme))
            setSingleChoiceItems(themeItems, currentAppTheme) { dialogInterface, index ->
                setAppTheme(index)
                dialogInterface.dismiss()
            }
        }.show()
    }

    private fun donateOnClick() = binding.clSettingsDonate.setOnClickListener {
        openLink(requireContext(), binding.root, URL_DONATE)
    }

    private fun privacyPolicyOnClick() = binding.clSettingsPrivacyPolicy.setOnClickListener {
        openLink(requireContext(), binding.root, URL_PRIVACY_POLICY)
    }

    private fun logoutOnClick() = binding.btnLogout.setOnClickListener {
        logout()
    }

    private fun setAppLocale(index: Int) = viewModel.setAppLocale(index, requireActivity())

    private fun setAppTheme(index: Int) = viewModel.setAppTheme(index)

    private fun sendVerificationEmail() = viewModel.sendVerificationEmail()

    private fun sendVerificationEmailObserver() =
        viewModel.sendVerificationEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.settings_dialog_message))
                            setCancelable(false)
                            setPositiveButton(requireContext().getString(R.string.settings_dialog_positive)) { _, _ -> }
                        }.show()
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            snackbar = when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    showNetworkConnectionError(
                                        requireContext(), binding.root
                                    ) { sendVerificationEmail() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    show403Error(requireContext(), binding.root)
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    showFirebaseDeviceBlockedError(requireContext(), binding.root)
                                }
                                else -> {
                                    showNetworkFailureError(requireContext(), binding.root)
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun logout() = viewModel.logoutUser()

    private fun logoutObserver() =
        viewModel.logoutLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        startActivity(Intent(requireContext(), LandingActivity::class.java))
                        requireActivity().finish()
                    }
                    Status.ERROR -> {
                        /* NO-OP */
                    }
                    Status.LOADING -> {
                        /* NO-OP */
                    }
                }
            }
        }

    private fun showLoadingAnimation() {
        binding.btnAccountVerification.visibility = GONE
        binding.cpiVerify.visibility = VISIBLE

    }

    private fun hideLoadingAnimation() {
        binding.cpiVerify.visibility = GONE
        binding.btnAccountVerification.visibility = VISIBLE
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
            appLovinAdRequestCounter = 1

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
            if (appLovinAdRequestCounter < 2) {
                appLovinAdRequestCounter++
                appLovinAdLoader.loadAd(createNativeAdView())
            } else {
                initTapsellAdHolder()
            }
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
                        tapsellRequestCounter = 1
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
                        if (tapsellRequestCounter < 2) {
                            tapsellRequestCounter++
                            requestTapsellNativeAd(adHolder)
                        }
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

    private fun showNativeAdContainer() {
        binding.flAdContainerNative.visibility = VISIBLE
        binding.dividerSettingsFourth.visibility = VISIBLE
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