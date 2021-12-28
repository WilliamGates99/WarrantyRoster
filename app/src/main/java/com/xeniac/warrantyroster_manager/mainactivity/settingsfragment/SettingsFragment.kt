package com.xeniac.warrantyroster_manager.mainactivity.settingsfragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.Constants
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentSettingsBinding
import com.xeniac.warrantyroster_manager.landingactivity.LandingActivity
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import ir.tapsell.plus.*
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    private lateinit var settingsPrefs: SharedPreferences
    private lateinit var currentLanguage: String
    private lateinit var currentCountry: String
    private var currentTheme: Int = 0

    private var requestAdCounter = 0
    private var responseId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        navController = Navigation.findNavController(view)
        (requireContext() as MainActivity).showNavBar()

        settingsPrefs = requireContext()
            .getSharedPreferences(Constants.PREFERENCE_SETTINGS, Context.MODE_PRIVATE)
        currentLanguage = settingsPrefs
            .getString(Constants.PREFERENCE_LANGUAGE_KEY, "en").toString()
        currentCountry = settingsPrefs.getString(Constants.PREFERENCE_COUNTRY_KEY, "US").toString()
        currentTheme = settingsPrefs.getInt(Constants.PREFERENCE_THEME_KEY, 0)

        getAccountDetails()
        setCurrentLanguageText()
        setCurrentThemeText()
        verifyOnClick()
        changeEmailOnClick()
        changePasswordOnClick()
        languageOnClick()
        themeOnClick()
        privacyPolicyOnClick()
        logoutOnClick()
        adInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyAd()
        _binding = null
    }

    private fun getAccountDetails() = CoroutineScope(Dispatchers.IO).launch {
        try {
            currentUser?.let {
                var email = it.email.toString()
                var isVerified = it.isEmailVerified
                Log.i("getAccountDetails", "Current user is $email and isVerified: $isVerified")

                withContext(Dispatchers.Main) {
                    setAccountDetails(email, isVerified)
                }

                if (NetworkHelper.hasNetworkAccess(requireContext())) {
                    it.reload().await()
                    if (email != it.email || isVerified != it.isEmailVerified) {
                        email = it.email.toString()
                        isVerified = it.isEmailVerified
                        Log.i(
                            "getAccountDetails",
                            "Updated current user is $$email and isVerified: $isVerified"
                        )

                        withContext(Dispatchers.Main) {
                            setAccountDetails(email, isVerified)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("getAccountDetails", "Exception: ${e.message}")
        }
    }

    private fun setAccountDetails(email: String, isEmailVerified: Boolean) {
        binding.tvSettingsAccountEmail.text = email

        if (isEmailVerified) {
            binding.btnSettingsAccountEmail.isClickable = false
            binding.btnSettingsAccountEmail.text =
                requireContext().getString(R.string.settings_btn_account_verified)
            binding.btnSettingsAccountEmail.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.green)
            )
            binding.btnSettingsAccountEmail.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green20)
            binding.ivSettingsAccountEmail.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.green10)
            )
            binding.lavSettingsAccountEmail.repeatCount = 0
            binding.lavSettingsAccountEmail.speed = 0.60f
            binding.lavSettingsAccountEmail.setAnimation(R.raw.anim_account_verified)
        } else {
            binding.btnSettingsAccountEmail.isClickable = true
            binding.btnSettingsAccountEmail.text =
                requireContext().getString(R.string.settings_btn_account_verify)
            binding.btnSettingsAccountEmail.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.blue)
            )
            binding.btnSettingsAccountEmail.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.blue20)
            binding.ivSettingsAccountEmail.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.red10)
            )
            binding.lavSettingsAccountEmail.repeatCount = LottieDrawable.INFINITE
            binding.lavSettingsAccountEmail.speed = 1.00f
            binding.lavSettingsAccountEmail.setAnimation(R.raw.anim_account_not_verified)
        }
        binding.lavSettingsAccountEmail.playAnimation()
    }

    private fun setCurrentLanguageText() {
        when (currentLanguage) {
            "en" -> {
                binding.tvSettingsSettingsLanguageCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_language_english)
            }
            "fa" -> {
                binding.tvSettingsSettingsLanguageCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_language_persian)
            }
        }
    }

    private fun setCurrentThemeText() {
        when (currentTheme) {
            0 -> {
                binding.tvSettingsSettingsThemeCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_theme_default)
            }
            1 -> {
                binding.tvSettingsSettingsThemeCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_theme_light)
            }
            2 -> {
                binding.tvSettingsSettingsThemeCurrent.text =
                    requireContext().getString(R.string.settings_text_settings_theme_dark)
            }
        }
    }

    private fun verifyOnClick() {
        binding.btnSettingsAccountEmail.setOnClickListener {
            sendVerificationEmail()
        }
    }

    private fun sendVerificationEmail() {
        if (NetworkHelper.hasNetworkAccess(requireContext())) {
            sendVerificationEmailAuth()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { sendVerificationEmail() }
                show()
            }
        }
    }

    private fun sendVerificationEmailAuth() {
        showLoadingAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentUser?.let {
                    it.sendEmailVerification().await()
                    Log.i("sendVerificationEmail", "Email sent to ${it.email}")
                    withContext(Dispatchers.Main) {
                        hideLoadingAnimation()

                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.settings_dialog_message))
                            setPositiveButton(requireContext().getString(R.string.settings_dialog_positive)) { _, _ -> }
                        }.show()
                    }
                }
            } catch (e: Exception) {
                Log.e("sendVerificationEmail", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    Snackbar.make(
                        binding.root,
                        requireContext().getString(R.string.network_error_failure),
                        LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun changeEmailOnClick() {
        binding.clSettingsAccountChangeEmail.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_changeEmailFragment)
        }
    }

    private fun changePasswordOnClick() {
        binding.clSettingsAccountChangePassword.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_changePasswordFragment)
        }
    }

    private fun languageOnClick() {
        binding.clSettingsSettingsLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Language", Toast.LENGTH_SHORT).show()
        }
    }

    private fun themeOnClick() {
        binding.clSettingsSettingsTheme.setOnClickListener {
            val themeItems = arrayOf(
                requireContext().getString(R.string.settings_text_settings_theme_default),
                requireContext().getString(R.string.settings_text_settings_theme_light),
                requireContext().getString(R.string.settings_text_settings_theme_dark)
            )

            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(requireContext().getString(R.string.settings_text_settings_theme))
                setSingleChoiceItems(themeItems, currentTheme) { dialogInterface, i ->
                    when (i) {
                        0 -> {
                            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                                settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i)
                                    .apply()
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            }
                        }
                        1 -> {
                            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                                settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i)
                                    .apply()
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            }
                        }
                        2 -> {
                            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                                settingsPrefs.edit().putInt(Constants.PREFERENCE_THEME_KEY, i)
                                    .apply()
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                        }
                    }
                    currentTheme = i
                    setCurrentThemeText()
                    dialogInterface.dismiss()
                }
            }.show()
        }
    }

    private fun privacyPolicyOnClick() {
        binding.clSettingsSettingsPrivacyPolicy.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_PRIVACY_POLICY)).apply {
                this.resolveActivity(requireContext().packageManager)?.let {
                    startActivity(this)
                } ?: Snackbar.make(
                    binding.root,
                    requireContext().getString(R.string.intent_error_app_not_found),
                    LENGTH_LONG
                ).show()
            }
        }
    }

    private fun logoutOnClick() {
        binding.btnSettingsLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() = CoroutineScope(Dispatchers.IO).launch {
        try {
            firebaseAuth.signOut()
            Log.i("logout", "${currentUser?.email} successfully logged out.")

            requireContext().getSharedPreferences(
                Constants.PREFERENCE_LOGIN, Context.MODE_PRIVATE
            ).edit().apply {
                remove(Constants.PREFERENCE_IS_LOGGED_IN_KEY)
                apply()
            }

            startActivity(Intent(requireContext(), LandingActivity::class.java))
            requireActivity().finish()
        } catch (e: Exception) {
            Log.e("logout", "Exception: ${e.message}")
        }
    }

    private fun showLoadingAnimation() {
        binding.btnSettingsAccountEmail.visibility = GONE
        binding.cpiSettingsAccountEmailVerification.visibility = VISIBLE

    }

    private fun hideLoadingAnimation() {
        binding.cpiSettingsAccountEmailVerification.visibility = GONE
        binding.btnSettingsAccountEmail.visibility = VISIBLE
    }

    private fun adInit() {
        TapsellPlus.initialize(
            requireContext(), Constants.TAPSELL_KEY, object : TapsellPlusInitListener {
                override fun onInitializeSuccess(adNetworks: AdNetworks?) {
                    Log.i("adInit", "onInitializeSuccess: ${adNetworks?.name}")
                    val adHolder = TapsellPlus.createAdHolder(
                        activity, binding.flSettingsAdContainer, R.layout.ad_banner_settings
                    )
                    requestAdCounter = 0
                    adHolder?.let { requestNativeAd(it) }
                }

                override fun onInitializeFailed(
                    adNetworks: AdNetworks?, adNetworkError: AdNetworkError?
                ) {
                    Log.e(
                        "adInit",
                        "onInitializeFailed: ${adNetworks?.name}, error: ${adNetworkError?.errorMessage}"
                    )
                }
            })
    }

    private fun requestNativeAd(adHolder: AdHolder) {
        TapsellPlus.requestNativeAd(requireActivity(),
            Constants.SETTINGS_NATIVE_ZONE_ID, object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel?) {
                    super.response(tapsellPlusAdModel)
                    Log.i("requestNativeAd", "response: ${tapsellPlusAdModel.toString()}")
                    _binding?.let {
                        tapsellPlusAdModel?.let {
                            responseId = it.responseId
                            showNativeAd(adHolder, responseId!!)
                        }
                    }
                }

                override fun error(error: String?) {
                    super.error(error)
                    Log.e("requestNativeAd", "Error: $error")
                    if (requestAdCounter < 3) {
                        requestAdCounter++
                        requestNativeAd(adHolder)
                    }
                }
            })
    }

    private fun showNativeAd(adHolder: AdHolder, responseId: String) {
        binding.groupSettingsAd.visibility = VISIBLE
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

    private fun destroyAd() {
        responseId?.let {
            TapsellPlus.destroyNativeBanner(requireActivity(), it)
        }
    }
}