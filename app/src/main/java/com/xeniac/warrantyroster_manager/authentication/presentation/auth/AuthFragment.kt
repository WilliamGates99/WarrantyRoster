package com.xeniac.warrantyroster_manager.authentication.presentation.auth

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
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.core.presentation.landing.LandingActivity
import com.xeniac.warrantyroster_manager.databinding.FragmentAuthBinding
import com.xeniac.warrantyroster_manager.util.AlertDialogHelper
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_INDEX_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.SnackBarHelper.showSomethingWentWrongError

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: AuthViewModel

    private var currentAppLocaleIndex = 0

    var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        subscribeToObservers()
        headerSetup()
        getCurrentAppLocaleIndex()
        languageOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToObservers() {
        currentAppLocaleIndexObserver()
        changeCurrentAppLocaleObserver()
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

    private fun getCurrentAppLocaleIndex() = viewModel.getCurrentAppLocaleIndex()

    private fun currentAppLocaleIndexObserver() =
        viewModel.currentAppLocaleIndexLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { localeIndex ->
                            currentAppLocaleIndex = localeIndex
                            setCurrentAppLocaleFlag(localeIndex)
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

    private fun setCurrentAppLocaleFlag(localeIndex: Int) {
        binding.apply {
            when (localeIndex) {
                LOCALE_INDEX_ENGLISH_UNITED_STATES -> ivLocaleFlag.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_flag_usa)
                )
                LOCALE_INDEX_ENGLISH_GREAT_BRITAIN -> ivLocaleFlag.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_flag_gb)
                )
                LOCALE_INDEX_PERSIAN_IRAN -> ivLocaleFlag.setImageDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_flag_iran)
                )
            }
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
            currentAppLocaleIndex
        ) { index ->
            changeCurrentAppLocale(index)
        }
    }

    private fun changeCurrentAppLocale(index: Int) = viewModel.changeCurrentAppLocale(index)

    private fun changeCurrentAppLocaleObserver() =
        viewModel.changeCurrentAppLocaleLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        response.data?.let { isActivityRestartNeeded ->
                            if (isActivityRestartNeeded) {
                                requireActivity().apply {
                                    startActivity(Intent(this, LandingActivity::class.java))
                                    finish()
                                }
                            } else {
                                getCurrentAppLocaleIndex()
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
}