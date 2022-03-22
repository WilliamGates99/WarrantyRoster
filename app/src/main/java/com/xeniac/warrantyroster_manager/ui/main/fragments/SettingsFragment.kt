package com.xeniac.warrantyroster_manager.ui.main.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adcolony.sdk.*
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.di.CurrentCountry
import com.xeniac.warrantyroster_manager.di.CurrentLanguage
import com.xeniac.warrantyroster_manager.models.Status
import com.xeniac.warrantyroster_manager.ui.landing.LandingActivity
import com.xeniac.warrantyroster_manager.ui.main.viewmodels.SettingsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ADCOLONY_BANNER_TEST_ZONE_ID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.TAPSELL_SETTINGS_NATIVE_ZONE_ID
import com.xeniac.warrantyroster_manager.utils.Constants.URL_DONATE
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.*
import ir.tapsell.plus.model.TapsellPlusAdModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    @CurrentLanguage
    lateinit var currentLanguage: String

    @Inject
    @CurrentCountry
    lateinit var currentCountry: String

    @set:Inject
    var currentTheme = 0

    private var requestAdCounter = 0
    private var responseId: String? = null

    private var adColonyBanner: AdColonyAdView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        getAccountDetails()
        accountDetailsObserver()
        setCurrentLanguageText()
        setCurrentThemeText()
        verifyOnClick()
        changeEmailOnClick()
        changePasswordOnClick()
        languageOnClick()
        themeOnClick()
        donateOnClick()
        privacyPolicyOnClick()
        logoutOnClick()
        subscribeToObservers()
        requestAdColonyBanner()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyAd()
        _binding = null
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
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
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

    private fun setCurrentLanguageText() {
        when (currentLanguage) {
            "en" -> {
                binding.tvSettingsLanguageCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_language_english)
            }
            "fa" -> {
                binding.tvSettingsLanguageCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_language_persian)
            }
        }
    }

    private fun setCurrentThemeText() {
        when (currentTheme) {
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
        findNavController().navigate(R.id.action_settingsFragment_to_changeEmailFragment)
    }

    private fun changePasswordOnClick() =
        binding.clAccountChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
        }

    private fun languageOnClick() = binding.clSettingsLanguage.setOnClickListener {
        Toast.makeText(requireContext(), "Language", Toast.LENGTH_SHORT).show()
    }

    private fun themeOnClick() = binding.clSettingsTheme.setOnClickListener {
        val themeItems = arrayOf(
            requireContext().getString(R.string.settings_text_settings_theme_default),
            requireContext().getString(R.string.settings_text_settings_theme_light),
            requireContext().getString(R.string.settings_text_settings_theme_dark)
        )

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(requireContext().getString(R.string.settings_text_settings_theme))
            setSingleChoiceItems(themeItems, currentTheme) { dialogInterface, index ->
                setAppTheme(index)
                currentTheme = index
                setCurrentThemeText()
                dialogInterface.dismiss()
            }
        }.show()
    }

    private fun donateOnClick() =
        binding.clSettingsDonate.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(URL_DONATE)).apply {
                this.resolveActivity(requireContext().packageManager)?.let {
                    startActivity(this)
                } ?: Snackbar.make(
                    binding.root,
                    requireContext().getString(R.string.intent_error_app_not_found),
                    LENGTH_LONG
                ).show()
            }
        }

    private fun privacyPolicyOnClick() =
        binding.clSettingsPrivacyPolicy.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIVACY_POLICY)).apply {
                this.resolveActivity(requireContext().packageManager)?.let {
                    startActivity(this)
                } ?: Snackbar.make(
                    binding.root,
                    requireContext().getString(R.string.intent_error_app_not_found),
                    LENGTH_LONG
                ).show()
            }
        }

    private fun logoutOnClick() = binding.btnLogout.setOnClickListener {
        logout()
    }

    private fun subscribeToObservers() {
        sendVerificationEmailObserver()
        logoutObserver()
    }

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
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_connection),
                                        LENGTH_LONG
                                    ).apply {
                                        setAction(requireContext().getString(R.string.network_error_retry)) { sendVerificationEmail() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_NETWORK_403) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_403),
                                        LENGTH_LONG
                                    ).show()
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.firebase_error_device_blocked),
                                        LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.network_error_failure),
                                        LENGTH_LONG
                                    ).show()
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
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
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

    //TODO REPLACE ADCOLONY_BANNER_SETTINGS_ZONE_ID FOR RELEASE
    private fun requestAdColonyBanner() = _binding?.let {
        AdColony.requestAdView(
            ADCOLONY_BANNER_TEST_ZONE_ID,
            object : AdColonyAdViewListener() {
                override fun onRequestFilled(ad: AdColonyAdView?) {
                    Timber.i("Banner request filled.")
                    adColonyBanner = ad
                    adColonyBanner?.let {
                        showAdColonyContainer()
                        binding.flAdContainerAdcolony.addView(it)
                    }
                }

                override fun onRequestNotFilled(zone: AdColonyZone?) {
                    super.onRequestNotFilled(zone)
                    Timber.e("Banner request did not fill.")
                    initTapsellAdHolder()
                }
            }, AdColonyAdSize.BANNER
        )
    }

    @Suppress("SpellCheckingInspection")
    private fun initTapsellAdHolder() {
        _binding?.let {
            val adHolder = TapsellPlus.createAdHolder(
                requireActivity(), binding.flAdContainerTapsell, R.layout.ad_banner_settings
            )

            requestAdCounter = 0
            adHolder?.let { requestTapsellNativeAd(it) }
        }
    }

    @Suppress("SpellCheckingInspection")
    private fun requestTapsellNativeAd(adHolder: AdHolder) {
        _binding?.let {
            TapsellPlus.requestNativeAd(requireActivity(),
                TAPSELL_SETTINGS_NATIVE_ZONE_ID, object : AdRequestCallback() {
                    override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                        super.response(tapsellPlusAdModel)
                        Timber.i("RequestNativeAd Response: ${tapsellPlusAdModel.toString()}")
                        requestAdCounter = 0
                        _binding?.let {
                            tapsellPlusAdModel?.let {
                                responseId = it.responseId
                                showNativeAd(adHolder, responseId!!)
                            }
                        }
                    }

                    override fun error(error: String?) {
                        super.error(error)
                        Timber.e("RequestNativeAd Error: $error")
                        if (requestAdCounter < 3) {
                            requestAdCounter++
                            requestTapsellNativeAd(adHolder)
                        }
                    }
                })
        }
    }

    private fun showNativeAd(adHolder: AdHolder, responseId: String) {
        _binding?.let {
            showTapsellContainer()
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

    private fun showAdColonyContainer() {
        binding.flAdContainerTapsell.visibility = GONE
        binding.flAdContainerAdcolony.visibility = VISIBLE
        binding.dividerSettingsFourth.visibility = VISIBLE
    }

    @Suppress("SpellCheckingInspection")
    private fun showTapsellContainer() {
        binding.flAdContainerAdcolony.visibility = GONE
        binding.flAdContainerTapsell.visibility = VISIBLE
        binding.dividerSettingsFourth.visibility = VISIBLE
    }

    private fun destroyAd() {
        adColonyBanner?.destroy()

        responseId?.let {
            TapsellPlus.destroyNativeBanner(requireActivity(), it)
        }
    }
}