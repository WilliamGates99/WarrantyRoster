package com.xeniac.warrantyroster_manager.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import coil.load
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentAuthBinding
import com.xeniac.warrantyroster_manager.ui.LandingActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_PERSIAN_IRAN

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: LandingViewModel

    private var currentLocaleIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        subscribeToObservers()
        getCurrentLanguage()
        headerSetup()
        languageOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToObservers() {
        currentAppLocaleObserver()
        changeCurrentLocaleObserver()
    }

    private fun getCurrentLanguage() = viewModel.getCurrentLanguage()

    private fun currentAppLocaleObserver() =
        viewModel.currentLocaleIndexLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { index ->
                currentLocaleIndex = index
                setCurrentLanguageFlag(index)
            }
        }

    private fun setCurrentLanguageFlag(index: Int) {
        binding.apply {
            when (index) {
                LOCALE_INDEX_ENGLISH_UNITED_STATES -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_flag_usa)
                )
                LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_flag_gb)
                )
                LOCALE_INDEX_PERSIAN_IRAN -> ivLanguageFlag.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_flag_iran)
                )
            }
        }
    }

    private fun headerSetup() = binding.apply {
        val navHostFragment = childFragmentManager
            .findFragmentById(binding.fcv.id) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                    setHeaderInfo(R.drawable.ic_auth_login, R.string.login_text_title)
                }
                R.id.registerFragment -> {
                    setHeaderInfo(R.drawable.ic_auth_register, R.string.register_text_title)
                }
                R.id.forgotPwFragment -> {
                    setHeaderInfo(R.drawable.ic_auth_forgot_pw, R.string.forgot_pw_text_title)
                }
                R.id.forgotPwSentFragment -> {
                    setHeaderInfo(R.drawable.ic_auth_forgot_pw, R.string.forgot_pw_sent_text_title)
                }
            }
        }
    }

    private fun setHeaderInfo(@DrawableRes drawable: Int, @StringRes title: Int) {
        binding.apply {
            ivHeader.load(drawable)
            tvTitle.text = getString(title)
        }
    }

    private fun languageOnClick() = binding.clLanguage.setOnClickListener {
        val localeTextItems = arrayOf(
            getString(R.string.auth_dialog_item_language_english_us),
            getString(R.string.auth_dialog_item_language_english_gb),
            getString(R.string.auth_dialog_item_language_persian_ir)
        )

        AlertDialogHelper.showSingleChoiceItemsDialog(
            requireContext(),
            R.string.auth_dialog_title_language,
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
                        startActivity(Intent(this, LandingActivity::class.java))
                        finish()
                    }
                } else {
                    viewModel.getCurrentLanguage()
                }
            }
        }
}