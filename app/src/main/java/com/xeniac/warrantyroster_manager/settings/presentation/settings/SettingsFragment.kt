package com.xeniac.warrantyroster_manager.settings.presentation.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.data.repository.NetworkConnectivityObserver
import com.xeniac.warrantyroster_manager.core.domain.repository.ConnectivityObserver
import com.xeniac.warrantyroster_manager.core.presentation.landing.LandingActivity
import com.xeniac.warrantyroster_manager.core.presentation.main.MainActivity
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper.showOneBtnAlertDialog
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper.showSingleChoiceItemsDialog
import com.xeniac.warrantyroster_manager.util.Constants.APPLOVIN_SETTINGS_NATIVE_UNIT_ID
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_AUTH_EMAIL_VERIFICATION_EMAIL_NOT_PROVIDED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.util.Constants.TAPSELL_SETTINGS_NATIVE_ZONE_ID
import com.xeniac.warrantyroster_manager.util.Constants.URL_CROWDIN
import com.xeniac.warrantyroster_manager.util.Constants.URL_DONATE
import com.xeniac.warrantyroster_manager.util.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.util.LinkHelper.openAppPageInStore
import com.xeniac.warrantyroster_manager.util.LinkHelper.openLink
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showNormalSnackbarError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showUnavailableNetworkConnectionError
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class SettingsFragment @Inject constructor(
    var viewModel: SettingsViewModel?
) : Fragment(R.layout.fragment_settings), MaxAdRevenueListener {

    private var _binding: FragmentSettingsBinding? = null
    val binding get() = _binding!!

    private var currentAppLocaleIndex = 0
    private var currentAppThemeIndex = 0

    private lateinit var appLovinNativeAdContainer: ViewGroup
    private lateinit var appLovinAdLoader: MaxNativeAdLoader
    private var appLovinNativeAd: MaxAd? = null

    private var tapsellResponseId: String? = null

    private lateinit var connectivityObserver: ConnectivityObserver
    private var networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.UNAVAILABLE

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        viewModel = viewModel ?: ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
        connectivityObserver = NetworkConnectivityObserver(requireContext())

        networkConnectivityObserver()
        subscribeToObservers()
        getUserInfo()
        getCurrentAppLocaleIndex()
        getCurrentAppLocaleUiText()
        getCurrentAppThemeIndex()
        getCurrentAppThemeUiText()
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

    private fun networkConnectivityObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityObserver.observe().onEach {
                networkStatus = it
                Timber.i("Network connectivity status inside of observer is $it")
            }.launchIn(lifecycleScope)
        } else {
            networkStatus = ConnectivityObserver.Status.AVAILABLE
        }
    }

    private fun subscribeToObservers() {
        userInfoObserver()
        currentAppLocaleIndexObserver()
        currentAppLocaleUiTextObserver()
        currentAppThemeIndexObserver()
        currentAppThemeUiTextObserver()
        changeCurrentAppLocaleObserver()
        changeCurrentAppThemeObserver()
        sendVerificationEmailObserver()
        logoutObserver()
    }

    private fun getUserInfo() = CoroutineScope(Dispatchers.IO).launch {
        getCachedUserInfo()

        /*
        Delay solved the issue that even though networkStatus was Available,
        the getReloadedAccountInfo() wouldn't be called.
         */
        delay(1.seconds)
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            Timber.i("Network Status is $networkStatus")
            getReloadedAccountInfo()
        } else {
            Timber.e("Network Status is $networkStatus")
        }
    }

    private fun getCachedUserInfo() = viewModel!!.getCachedUserInfo()

    private fun getReloadedAccountInfo() = viewModel!!.getReloadedUserInfo()

    private fun userInfoObserver() =
        viewModel!!.userInfoLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        /* NO-OP */
                    }
                    is Resource.Success -> {
                        response.data?.let { userInfo ->
                            setAccountDetails(
                                userInfo.email.toString(),
                                userInfo.isEmailVerified
                            )
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(requireContext(), requireView())
                    }
                }
            }
        }

    private fun setAccountDetails(email: String, isEmailVerified: Boolean) {
        binding.apply {
            val shouldUpdateUi = email != userEmail || isEmailVerified != isUserVerified
            if (shouldUpdateUi) {
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
    }

    private fun getCurrentAppLocaleIndex() = viewModel!!.getCurrentAppLocaleIndex()

    private fun currentAppLocaleIndexObserver() =
        viewModel!!.currentAppLocaleIndexLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { localeIndex ->
                            currentAppLocaleIndex = localeIndex
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(
                            requireContext(), requireView()
                        )
                    }
                }
            }
        }

    private fun getCurrentAppLocaleUiText() = viewModel!!.getCurrentAppLocaleUiText()

    private fun currentAppLocaleUiTextObserver() =
        viewModel!!.currentAppLocaleUiTextLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { localeUiText ->
                            binding.currentLanguage = localeUiText.asString(requireContext())
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(
                            requireContext(), requireView()
                        )
                    }
                }
            }
        }

    private fun getCurrentAppThemeIndex() = viewModel!!.getCurrentAppThemeIndex()

    private fun currentAppThemeIndexObserver() =
        viewModel!!.currentAppThemeIndexLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { currentThemeIndex ->
                            currentAppThemeIndex = currentThemeIndex
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(
                            requireContext(), requireView()
                        )
                    }
                }
            }
        }

    private fun getCurrentAppThemeUiText() = viewModel!!.getCurrentAppThemeUiText()

    private fun currentAppThemeUiTextObserver() =
        viewModel!!.currentAppThemeUiTextLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { themeUiText ->
                            binding.currentTheme = themeUiText.asString(requireContext())
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(
                            requireContext(), requireView()
                        )
                    }
                }
            }
        }

    private fun verifyOnClick() = binding.btnAccountVerification.setOnClickListener {
        sendVerificationEmail()
    }

    private fun sendVerificationEmail() {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModel!!.sendVerificationEmail()
        } else {
            snackbar = showUnavailableNetworkConnectionError(
                requireContext(), requireView()
            ) { sendVerificationEmail() }
            Timber.e("sendVerificationEmail Error: Offline")
        }
    }

    private fun sendVerificationEmailObserver() =
        viewModel!!.sendVerificationEmailLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
        navigateToLinkedAccountsFragment()
    }

    private fun navigateToLinkedAccountsFragment() = findNavController().navigate(
        SettingsFragmentDirections.actionSettingsFragmentToLinkedAccountsFragment()
    )

    private fun changeEmailOnClick() = binding.clAccountChangeEmail.setOnClickListener {
        navigateToChangeEmailFragment()
    }

    private fun navigateToChangeEmailFragment() = findNavController().navigate(
        SettingsFragmentDirections.actionSettingsFragmentToChangeEmailFragment()
    )

    private fun changePasswordOnClick() = binding.clAccountChangePassword.setOnClickListener {
        navigateToChangePasswordFragment()
    }

    private fun navigateToChangePasswordFragment() = findNavController().navigate(
        SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment()
    )

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
            currentAppLocaleIndex
        ) { index ->
            changeCurrentAppLocale(index)
        }
    }

    private fun changeCurrentAppLocale(index: Int) = viewModel!!.changeCurrentAppLocale(index)

    private fun changeCurrentAppLocaleObserver() =
        viewModel!!.changeCurrentAppLocaleLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { isActivityRestartNeeded ->
                            if (isActivityRestartNeeded) {
                                requireActivity().apply {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                            } else {
                                getCurrentAppLocaleIndex()
                                getCurrentAppLocaleUiText()
                            }
                        }
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(
                            requireContext(), requireView()
                        )
                    }
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
            currentAppThemeIndex
        ) { index ->
            changeCurrentAppTheme(index)
        }
    }

    private fun changeCurrentAppTheme(index: Int) = viewModel!!.changeCurrentAppTheme(index)

    private fun changeCurrentAppThemeObserver() =
        viewModel!!.changeCurrentAppThemeLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        getCurrentAppThemeIndex()
                        getCurrentAppThemeUiText()
                    }
                    is Resource.Error -> {
                        snackbar = showSomethingWentWrongError(
                            requireContext(), requireView()
                        )
                    }
                }
            }
        }

    private fun donateOnClick() = binding.clSettingsDonate.setOnClickListener {
        openLink(requireContext(), requireView(), URL_DONATE)
    }

    private fun improveTranslationsOnClick() =
        binding.clSettingsImproveTranslations.setOnClickListener {
            openLink(requireContext(), requireView(), URL_CROWDIN)
        }

    private fun rateUsOnClick() = binding.clSettingsRateUs.setOnClickListener {
        openAppPageInStore(requireContext(), requireView())
    }

    private fun privacyPolicyOnClick() = binding.clSettingsPrivacyPolicy.setOnClickListener {
        openLink(requireContext(), requireView(), URL_PRIVACY_POLICY)
    }

    private fun logoutOnClick() = binding.btnLogout.setOnClickListener {
        logoutUser()
    }

    private fun logoutUser() = viewModel!!.logoutUser()

    private fun logoutObserver() =
        viewModel!!.logoutLiveData.observe(viewLifecycleOwner) { responseEvent ->
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
        appLovinAdLoader = MaxNativeAdLoader(
            APPLOVIN_SETTINGS_NATIVE_UNIT_ID,
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
            TapsellPlus.requestNativeAd(
                requireActivity(),
                TAPSELL_SETTINGS_NATIVE_ZONE_ID,
                object : AdRequestCallback() {
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
                }
            )
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