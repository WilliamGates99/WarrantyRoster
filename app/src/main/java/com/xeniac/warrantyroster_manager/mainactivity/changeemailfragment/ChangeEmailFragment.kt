package com.xeniac.warrantyroster_manager.mainactivity.changeemailfragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.NetworkHelper
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentChangeEmailBinding
import com.xeniac.warrantyroster_manager.mainactivity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChangeEmailFragment : Fragment(R.layout.fragment_change_email) {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeEmailBinding.bind(view)
        navController = Navigation.findNavController(view)
        (requireContext() as MainActivity).hideNavBar()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnToMainActivity()
        changeEmailOnClick()
        changeEmailActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiChangeEmailEditCurrentEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiChangeEmailLayoutCurrentEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiChangeEmailLayoutCurrentEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiChangeEmailEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiChangeEmailLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiChangeEmailLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiChangeEmailEditNewEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiChangeEmailLayoutNewEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiChangeEmailLayoutNewEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiChangeEmailEditCurrentEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiChangeEmailLayoutCurrentEmail.isErrorEnabled = false
                binding.tiChangeEmailLayoutCurrentEmail.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiChangeEmailEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiChangeEmailLayoutPassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiChangeEmailEditNewEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiChangeEmailLayoutNewEmail.isErrorEnabled = false
                binding.tiChangeEmailLayoutNewEmail.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun returnToMainActivity() {
        binding.toolbarChangeEmail.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun changeEmailOnClick() {
        binding.btnChangeEmail.setOnClickListener {
            changeUserEmail()
        }
    }

    private fun changeEmailActionDone() {
        binding.tiChangeEmailEditNewEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changeUserEmail()
            }
            false
        }
    }

    private fun changeUserEmail() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (NetworkHelper.hasNetworkAccess(requireContext())) {
            getChangeUserEmailInputs()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { changeUserEmail() }
                show()
            }
        }
    }

    private fun getChangeUserEmailInputs() {
        val email = binding.tiChangeEmailEditCurrentEmail.text.toString().trim().lowercase()
        val password = binding.tiChangeEmailEditPassword.text.toString().trim()
        val newEmail = binding.tiChangeEmailEditNewEmail.text.toString().trim().lowercase()

        if (email.isBlank()) {
            binding.tiChangeEmailLayoutCurrentEmail.requestFocus()
            binding.tiChangeEmailLayoutCurrentEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (password.isBlank()) {
            binding.tiChangeEmailLayoutPassword.requestFocus()
            binding.tiChangeEmailLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (newEmail.isBlank()) {
            binding.tiChangeEmailLayoutNewEmail.requestFocus()
            binding.tiChangeEmailLayoutNewEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiChangeEmailLayoutCurrentEmail.requestFocus()
                binding.tiChangeEmailLayoutCurrentEmail.error =
                    requireContext().getString(R.string.change_email_error_email)
            } else if (!isEmailValid(newEmail)) {
                binding.tiChangeEmailLayoutNewEmail.requestFocus()
                binding.tiChangeEmailLayoutNewEmail.error =
                    requireContext().getString(R.string.change_email_error_new_email)
            } else if (newEmail == currentUser?.email) {
                binding.tiChangeEmailLayoutNewEmail.requestFocus()
                binding.tiChangeEmailLayoutNewEmail.error =
                    requireContext().getString(R.string.change_email_error_email_same)
            } else {
                changeUserEmailAuth(email, password, newEmail)
            }
        }
    }

    private fun changeUserEmailAuth(email: String, password: String, newEmail: String) {
        showLoadingAnimation()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentUser?.let {
                    val credential = EmailAuthProvider.getCredential(email, password)
                    it.reauthenticate(credential).await()
                    Log.i("changeUserEmail", "User re-authenticated.")

                    it.updateEmail(newEmail).await()
                    Log.i("changeUserEmail", "User email updated to ${newEmail}.")

                    withContext(Dispatchers.Main) {
                        hideLoadingAnimation()
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setMessage(requireContext().getString(R.string.change_email_dialog_message))
                            setPositiveButton(requireContext().getString(R.string.change_email_dialog_positive)) { _, _ -> }
                            setOnDismissListener { requireActivity().onBackPressed() }
                            show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("changeUserEmail", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    if (e.toString()
                            .contains("The password is invalid or the user does not have a password")
                        || e.toString()
                            .contains("The supplied credentials do not correspond to the previously signed in user")
                    ) {
                        Snackbar.make(
                            binding.root,
                            requireContext().getString(R.string.change_email_error_credentials),
                            LENGTH_INDEFINITE
                        ).show()
                    }
                    if (e.toString()
                            .contains("The email address is already in use by another account")
                    ) {
                        Snackbar.make(
                            binding.root,
                            requireContext().getString(R.string.change_email_error_email_exists),
                            LENGTH_INDEFINITE
                        ).show()
                    } else {
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

    private fun showLoadingAnimation() {
        binding.tiChangeEmailEditCurrentEmail.isEnabled = false
        binding.tiChangeEmailEditPassword.isEnabled = false
        binding.tiChangeEmailEditNewEmail.isEnabled = false
        binding.btnChangeEmail.isClickable = false
        binding.btnChangeEmail.text = null
        binding.cpiChangeEmail.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiChangeEmail.visibility = GONE
        binding.tiChangeEmailEditCurrentEmail.isEnabled = true
        binding.tiChangeEmailEditPassword.isEnabled = true
        binding.tiChangeEmailEditNewEmail.isEnabled = true
        binding.btnChangeEmail.isClickable = true
        binding.btnChangeEmail.text = requireContext().getString(R.string.change_email_btn_change)
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
}