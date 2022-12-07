package com.xeniac.warrantyroster_manager.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLinkedAccountsBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.LinkedAccountsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

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
        linkGoogleObserver()
        linkTwitterObserver()
        linkFacebookObserver()
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
        if (linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)) {
            showGoogleConnectedStatus()
        } else {
            showGoogleNotConnectedStatus()
        }

        if (linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_TWITTER)) {
            showTwitterConnectedStatus()
        } else {
            showTwitterNotConnectedStatus()
        }

        if (linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)) {
            showFacebookConnectedStatus()
        } else {
            showFacebookNotConnectedStatus()
        }
    }

    private fun googleOnClick() = binding.cvGoogle.setOnClickListener {
        launchGoogleSignInClient()
    }

    private fun launchGoogleSignInClient() = CoroutineScope(Dispatchers.Main).launch {
        try {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
                requestIdToken(BuildConfig.GOOGLE_AUTH_SERVER_CLIENT_ID)
                requestId()
            }.build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), options)
            googleSignInClient.signOut().await()

            googleResultLauncher.launch(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            Timber.e("await exception: $e")
        }
    }

    private val googleResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            // Got an ID token from Google. Use it to authenticate with Firebase.
            account?.let { viewModel.linkGoogleAccount(account) }
        } catch (e: Exception) {
            Timber.e("googleResultLauncher Exception: ${e.message}")
        }
    }

    private fun linkGoogleObserver() =
        viewModel.linkGoogleLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showGoogleLoadingAnimation()
                    is Resource.Success -> {
                        hideGoogleLoadingAnimation()
                        showGoogleConnectedStatus()
                    }
                    is Resource.Error -> {
                        hideGoogleLoadingAnimation()
                        showGoogleNotConnectedStatus()
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

    private fun twitterOnClick() = binding.cvTwitter.setOnClickListener {
        linkTwitterAccount()
    }

    private fun linkTwitterObserver() =
        viewModel.linkTwitterLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showTwitterLoadingAnimation()
                    is Resource.Success -> {
                        hideTwitterLoadingAnimation()
                    }
                    is Resource.Error -> {
                        hideTwitterLoadingAnimation()
                    }
                }
            }
        }

    private fun linkTwitterAccount() = viewModel.linkTwitterAccount()

    private fun facebookOnClick() = binding.cvFacebook.setOnClickListener {
        linkFacebookAccount()
    }

    private fun linkFacebookAccount() = viewModel.linkFacebookAccount()

    private fun linkFacebookObserver() =
        viewModel.linkFacebookLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showFacebookLoadingAnimation()
                    is Resource.Success -> {
                        hideFacebookLoadingAnimation()
                    }
                    is Resource.Error -> {
                        hideFacebookLoadingAnimation()
                    }
                }
            }
        }

    private fun showGoogleConnectedStatus() = binding.apply {
        googleClickable = false
        googleStatusBackground = AppCompatResources.getDrawable(
            requireContext(), R.drawable.shape_rounded_linked_accounts_status_connected
        )
        googleStatusTextColor = ContextCompat.getColor(requireContext(), R.color.green)
        googleStatusText = requireContext().getString(R.string.linked_accounts_status_connected)
    }

    private fun showGoogleNotConnectedStatus() = binding.apply {
        googleClickable = true
        googleStatusBackground = AppCompatResources.getDrawable(
            requireContext(), R.drawable.shape_rounded_linked_accounts_status_not_connected
        )
        googleStatusTextColor = ContextCompat.getColor(requireContext(), R.color.grayDark)
        googleStatusText = requireContext().getString(R.string.linked_accounts_status_not_connected)
    }

    private fun showTwitterConnectedStatus() = binding.apply {
        twitterClickable = false
        twitterStatusBackground = AppCompatResources.getDrawable(
            requireContext(), R.drawable.shape_rounded_linked_accounts_status_connected
        )
        twitterStatusTextColor = ContextCompat.getColor(requireContext(), R.color.green)
        twitterStatusText = requireContext().getString(R.string.linked_accounts_status_connected)
    }

    private fun showTwitterNotConnectedStatus() = binding.apply {
        twitterClickable = true
        twitterStatusBackground = AppCompatResources.getDrawable(
            requireContext(), R.drawable.shape_rounded_linked_accounts_status_not_connected
        )
        twitterStatusTextColor = ContextCompat.getColor(requireContext(), R.color.grayDark)
        twitterStatusText = requireContext().getString(
            R.string.linked_accounts_status_not_connected
        )
    }

    private fun showFacebookConnectedStatus() = binding.apply {
        facebookClickable = false
        facebookStatusBackground = AppCompatResources.getDrawable(
            requireContext(), R.drawable.shape_rounded_linked_accounts_status_connected
        )
        facebookStatusTextColor = ContextCompat.getColor(requireContext(), R.color.green)
        facebookStatusText = requireContext().getString(R.string.linked_accounts_status_connected)
    }

    private fun showFacebookNotConnectedStatus() = binding.apply {
        facebookClickable = true
        facebookStatusBackground = AppCompatResources.getDrawable(
            requireContext(), R.drawable.shape_rounded_linked_accounts_status_not_connected
        )
        facebookStatusTextColor = ContextCompat.getColor(requireContext(), R.color.grayDark)
        facebookStatusText = requireContext().getString(
            R.string.linked_accounts_status_not_connected
        )
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