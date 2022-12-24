package com.xeniac.warrantyroster_manager.ui.fragments.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentOnboardingBinding
import com.xeniac.warrantyroster_manager.ui.LandingActivity
import com.xeniac.warrantyroster_manager.ui.adapters.OnBoardingAdapter
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.AlertDialogHelper.showSingleChoiceItemsDialog
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.utils.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.utils.Constants.ONBOARDING_1ST_INDEX
import com.xeniac.warrantyroster_manager.utils.Constants.ONBOARDING_2ND_INDEX
import com.xeniac.warrantyroster_manager.utils.Constants.ONBOARDING_3RD_INDEX
import com.xeniac.warrantyroster_manager.utils.Constants.ONBOARDING_4TG_INDEX

class OnBoardingFragment : Fragment(R.layout.fragment_onboarding) {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: LandingViewModel

    private var currentLocaleIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        setupViewPager()
        subscribeToObservers()
        getCurrentLanguage()
        languageOnClick()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            binding.viewpager.apply {
                when (currentItem) {
                    ONBOARDING_2ND_INDEX -> currentItem = ONBOARDING_1ST_INDEX
                    ONBOARDING_3RD_INDEX -> currentItem = ONBOARDING_2ND_INDEX
                    ONBOARDING_4TG_INDEX -> currentItem = ONBOARDING_3RD_INDEX
                }
            }
        }
    }

    private fun setupViewPager() {
        val fragmentsList = arrayListOf(
            OnBoarding1stFragment(),
            OnBoarding2ndFragment(),
            OnBoarding3rdFragment(),
            OnBoarding4thFragment()
        )

        val onBoardingAdapter = OnBoardingAdapter(
            fragmentsList, childFragmentManager, lifecycle
        )

        binding.viewpager.adapter = onBoardingAdapter
        binding.indicator.attachTo(binding.viewpager)
        viewPagerOnPageChange()
    }

    private fun viewPagerOnPageChange() =
        binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onBackPressedCallback.isEnabled = when (position) {
                    ONBOARDING_1ST_INDEX -> false
                    ONBOARDING_2ND_INDEX -> true
                    ONBOARDING_3RD_INDEX -> true
                    ONBOARDING_4TG_INDEX -> true
                    else -> false
                }
            }
        })

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

    private fun languageOnClick() = binding.clLanguage.setOnClickListener {
        val localeTextItems = arrayOf(
            getString(R.string.onboarding_dialog_item_language_english_us),
            getString(R.string.onboarding_dialog_item_language_english_gb),
            getString(R.string.onboarding_dialog_item_language_persian_ir)
        )

        showSingleChoiceItemsDialog(
            requireContext(),
            R.string.onboarding_dialog_title_language,
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