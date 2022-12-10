package com.xeniac.warrantyroster_manager.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentLinkedAccountsBinding
import com.xeniac.warrantyroster_manager.ui.viewmodels.LinkedAccountsViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import com.xeniac.warrantyroster_manager.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LinkedAccountsFragment : Fragment(R.layout.fragment_linked_accounts) {

    private var _binding: FragmentLinkedAccountsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: LinkedAccountsViewModel

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var currentAppLanguage: String

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLinkedAccountsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LinkedAccountsViewModel::class.java]

        toolbarNavigationBackOnClick()
        subscribeToObservers()
        getCurrentAppLanguage()
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
        currentAppLanguageObserver()
        linkedAccountsObserver()
        linkGoogleObserver()
        linkTwitterObserver()
        linkFacebookObserver()
    }

    /*
    TODO MOVE THIS TO ON_VIEW_CREATED AND CREATE ITS OWN FUNCTION AND LIVEDATA IN VIEW_MODEL
    TODO ALSO ADD TO LOGIN_FRAGMENT
      // There's something already here! Finish the sign-in for your user.
     firebaseAuth.pendingAuthResult?.let { pendingAuthResult ->
         Timber.i("FirebaseAuth pending twitter auth result is not null.")
         pendingAuthResult.addOnCompleteListener {
             if (it.isSuccessful) {
                 viewModel.linkTwitterAccount()
             } else {
                 hideTwitterLoadingAnimation()
                 // TODO SHOW ERROR
                 Timber.e("pendingResultTask is not null -> onFail:")
                 Timber.e("Exception: ${it.exception?.message}")
                 Timber.e("--------------------------------------------")
             }
         }
     }
      */

    private fun getCurrentAppLanguage() = viewModel.getCurrentAppLanguage()

    private fun currentAppLanguageObserver() =
        viewModel.currentLanguageLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { language ->
                currentAppLanguage = language
            }
        }

    private fun getLinkedAccounts() = viewModel.getLinkedAccounts()

    private fun linkedAccountsObserver() =
        viewModel.linkedAccountsLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showAllLoadingAnimations()
                    is Resource.Success -> {
                        hideAllLoadingAnimations()
                        response.data?.let { showLinkedAccountsStatus(it) }
                    }
                    is Resource.Error -> {
                        hideAllLoadingAnimations()

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

    private fun showLinkedAccountsStatus(linkedAccounts: List<String>) = binding.apply {
        isGoogleConnected = linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_GOOGLE)

        isTwitterConnected = linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_TWITTER)

        isFacebookConnected = linkedAccounts.contains(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK)
    }

    private fun googleOnClick() = binding.apply {
        cvGoogle.setOnClickListener {
            if (isGoogleConnected) unlinkGoogleAccount() else launchGoogleSignInClient()
        }
    }

    private fun unlinkGoogleAccount() {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser!!.unlink(FIREBASE_AUTH_PROVIDER_ID_GOOGLE).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("google removed from user: ${it.result.user}")
                getLinkedAccounts()
            } else {
                Timber.e("Unlink Google Exception: ${it.exception?.message}")
            }
        }
    }

    private fun launchGoogleSignInClient() = CoroutineScope(Dispatchers.Main).launch {
        try {
            showGoogleLoadingAnimation()
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
                requestIdToken(BuildConfig.GOOGLE_AUTH_SERVER_CLIENT_ID)
                requestId()
                requestEmail()
            }.build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), options)
            googleSignInClient.signOut().await()

            googleResultLauncher.launch(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            hideGoogleLoadingAnimation()
            // TODO EDIT
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
            hideGoogleLoadingAnimation()
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
                        binding.isGoogleConnected = true
                    }
                    is Resource.Error -> {
                        hideGoogleLoadingAnimation()
                        binding.isGoogleConnected = false
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

    private fun twitterOnClick() = binding.apply {
        cvTwitter.setOnClickListener {
            if (isTwitterConnected) unlinkTwitterAccount() else linkTwitterAccount()
        }
    }

    private fun unlinkTwitterAccount() {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser!!.unlink(FIREBASE_AUTH_PROVIDER_ID_TWITTER).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Twitter removed from user: ${it.result.user}")
                getLinkedAccounts()
            } else {
                Timber.e("Unlink Twitter Exception: ${it.exception?.message}")
            }
        }
    }

    private fun linkTwitterAccount() {
        showTwitterLoadingAnimation()

        val oAuthProvider = OAuthProvider.newBuilder(FIREBASE_AUTH_PROVIDER_ID_TWITTER)
        oAuthProvider.addCustomParameter("lang", currentAppLanguage)

        // TODO safeLinkTwitterAccount Exception: User has already been linked to the given provider.
        firebaseAuth.startActivityForSignInWithProvider(requireActivity(), oAuthProvider.build())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val authCredential = it.result.credential
                    authCredential?.let { credential ->
                        viewModel.linkTwitterAccount(credential)
                    }
                } else {
                    // TODO EDIT
                    hideTwitterLoadingAnimation()
                    it.exception?.message?.let { message ->
                        Timber.e("else -> onFail:")
                        Timber.e("Exception: $message")
                        Timber.e("--------------------------------------------")
                        if (message.contains("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.")) {
                            Toast.makeText(
                                requireContext(),
                                "An account already exists with the same email address but different sign-in credentials.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }

    private fun linkTwitterObserver() =
        viewModel.linkTwitterLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showTwitterLoadingAnimation()
                    is Resource.Success -> {
                        hideTwitterLoadingAnimation()
                        binding.isTwitterConnected = true
                    }
                    is Resource.Error -> {
                        hideTwitterLoadingAnimation()
                        binding.isTwitterConnected = false
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

    private fun facebookOnClick() = binding.apply {
        cvFacebook.setOnClickListener {
            if (isGoogleConnected) unlinkFacebookAccount() else linkFacebookAccount()
        }
    }

    private fun unlinkFacebookAccount() {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser!!.unlink(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("Facebook removed from user: ${it.result.user}")
                getLinkedAccounts()
            } else {
                Timber.e("Unlink Facebook Exception: ${it.exception?.message}")
            }
        }
    }

    private fun linkFacebookAccount() = viewModel.linkFacebookAccount()

    private fun linkFacebookObserver() =
        viewModel.linkFacebookLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showFacebookLoadingAnimation()
                    is Resource.Success -> {
                        hideFacebookLoadingAnimation()
                        binding.isFacebookConnected = true
                    }
                    is Resource.Error -> {
                        hideFacebookLoadingAnimation()
                        binding.isFacebookConnected = false
                    }
                }
            }
        }

    private fun showAllLoadingAnimations() {
        showGoogleLoadingAnimation()
        showTwitterLoadingAnimation()
        showFacebookLoadingAnimation()
    }

    private fun hideAllLoadingAnimations() {
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