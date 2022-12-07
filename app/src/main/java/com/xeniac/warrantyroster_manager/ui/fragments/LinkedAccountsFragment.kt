package com.xeniac.warrantyroster_manager.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLinkedAccountsBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.LinkedAccountsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_FACEBOOK
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_GOOGLE
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_TWITTER
import com.xeniac.warrantyroster_manager.utils.Resource

class LinkedAccountsFragment : Fragment(R.layout.fragment_linked_accounts) {

    private var _binding: FragmentLinkedAccountsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: LinkedAccountsViewModel

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLinkedAccountsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LinkedAccountsViewModel::class.java]

        toolbarNavigationBackOnClick()
        subscribeToObservers()
        getLinkedAccounts()
        googleOnClick()
        twitterOnClick()
        facebookOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    private fun toolbarNavigationBackOnClick() = binding.toolbar.setNavigationOnClickListener {
        navigateBack()
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    private fun subscribeToObservers() {
        linkedAccountsObserver()
    }

    private fun getLinkedAccounts() = viewModel.getLinkedAccounts()

    private fun linkedAccountsObserver() =
        viewModel.linkedAccountsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()
                        response.data?.let { showLinkedAccountsStatus(it) }
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()

                        binding.apply {
                            googleClickable = false
                            twitterClickable = false
                            facebookClickable = false
                        }

                        response.message?.asString(requireContext())?.let {
                            snackbar = Snackbar.make(requireView(), it, LENGTH_INDEFINITE).apply {
                                setAction(requireContext().getString(R.string.error_btn_retry)) { getLinkedAccounts() }
                                show()
                            }
                        }
                    }
                }
            }
        }

    private fun showLinkedAccountsStatus(linkedAccounts: List<String>) {
        if (linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_GOOGLE)) {
            setGoogleStatus(
                false,
                R.drawable.shape_rounded_linked_accounts_status_connected,
                R.color.green,
                R.string.linked_accounts_status_connected
            )
        } else {
            setGoogleStatus(
                true,
                R.drawable.shape_rounded_linked_accounts_status_not_connected,
                R.color.grayDark,
                R.string.linked_accounts_status_not_connected
            )
        }

        if (linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_TWITTER)) {
            setTwitterStatus(
                false,
                R.drawable.shape_rounded_linked_accounts_status_connected,
                R.color.green,
                R.string.linked_accounts_status_connected
            )
        } else {
            setTwitterStatus(
                true,
                R.drawable.shape_rounded_linked_accounts_status_not_connected,
                R.color.grayDark,
                R.string.linked_accounts_status_not_connected
            )
        }

        if (linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_FACEBOOK)) {
            setFacebookStatus(
                false,
                R.drawable.shape_rounded_linked_accounts_status_connected,
                R.color.green,
                R.string.linked_accounts_status_connected
            )
        } else {
            setFacebookStatus(
                true,
                R.drawable.shape_rounded_linked_accounts_status_not_connected,
                R.color.grayDark,
                R.string.linked_accounts_status_not_connected
            )
        }
    }

    private fun setGoogleStatus(
        isClickable: Boolean,
        @DrawableRes background: Int,
        @ColorRes textColor: Int,
        @StringRes text: Int
    ) = binding.apply {
        googleClickable = isClickable
        googleStatusBackground = AppCompatResources.getDrawable(requireContext(), background)
        googleStatusTextColor = ContextCompat.getColor(requireContext(), textColor)
        googleStatusText = requireContext().getString(text)
    }

    private fun setTwitterStatus(
        isClickable: Boolean,
        @DrawableRes background: Int,
        @ColorRes textColor: Int,
        @StringRes text: Int
    ) = binding.apply {
        twitterClickable = isClickable
        twitterStatusBackground = AppCompatResources.getDrawable(requireContext(), background)
        twitterStatusTextColor = ContextCompat.getColor(requireContext(), textColor)
        twitterStatusText = requireContext().getString(text)
    }

    private fun setFacebookStatus(
        isClickable: Boolean,
        @DrawableRes background: Int,
        @ColorRes textColor: Int,
        @StringRes text: Int
    ) = binding.apply {
        facebookClickable = isClickable
        facebookStatusBackground = AppCompatResources.getDrawable(requireContext(), background)
        facebookStatusTextColor = ContextCompat.getColor(requireContext(), textColor)
        facebookStatusText = requireContext().getString(text)
    }

    private fun googleOnClick() = binding.cvGoogle.setOnClickListener {
        Toast.makeText(requireContext(), "Google", Toast.LENGTH_SHORT).show()
    }

    private fun twitterOnClick() = binding.cvTwitter.setOnClickListener {
        Toast.makeText(requireContext(), "Twitter", Toast.LENGTH_SHORT).show()
    }

    private fun facebookOnClick() = binding.cvFacebook.setOnClickListener {
        Toast.makeText(requireContext(), "Facebook", Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingAnimation() {
        showGoogleLoadingAnimation()
        showTwitterLoadingAnimation()
        showFacebookLoadingAnimation()
    }

    private fun hideLoadingAnimation() {
        hideGoogleLoadingAnimation()
        hideTwitterLoadingAnimation()
        hideFacebookLoadingAnimation()
    }

    private fun showGoogleLoadingAnimation() = binding.apply {
        googleClickable = false
        twitterClickable = false
        facebookClickable = false
        isGoogleLoading = true
    }

    private fun hideGoogleLoadingAnimation() = binding.apply {
        isGoogleLoading = false
        googleClickable = true
        twitterClickable = true
        facebookClickable = true
    }

    private fun showTwitterLoadingAnimation() = binding.apply {
        googleClickable = false
        twitterClickable = false
        facebookClickable = false
        isTwitterLoading = true
    }

    private fun hideTwitterLoadingAnimation() = binding.apply {
        isTwitterLoading = false
        googleClickable = true
        twitterClickable = true
        facebookClickable = true
    }

    private fun showFacebookLoadingAnimation() = binding.apply {
        googleClickable = false
        twitterClickable = false
        facebookClickable = false
        isFacebookLoading = true
    }

    private fun hideFacebookLoadingAnimation() = binding.apply {
        isFacebookLoading = false
        googleClickable = true
        twitterClickable = true
        facebookClickable = true
    }
}