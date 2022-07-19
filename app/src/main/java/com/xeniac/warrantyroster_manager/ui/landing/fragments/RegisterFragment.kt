package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.ui.viewmodels.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_EMAIL_INVALID
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_NOT_MATCH
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_PASSWORD_SHORT
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_REGISTER_EMAIL
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_REGISTER_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_REGISTER_RETYPE_PASSWORD
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.utils.LinkHelper.openLink
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.show403Error
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseAuthAccountExists
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showFirebaseDeviceBlockedError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkConnectionError
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showNetworkFailureError
import com.xeniac.warrantyroster_manager.utils.Status
import com.xeniac.warrantyroster_manager.utils.UserHelper.passwordStrength
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding!!

    lateinit var viewModel: LandingViewModel

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        textInputsBackgroundColor()
        textInputsStrokeColor()
        agreementOnclick()
        loginOnClick()
        registerOnClick()
        registerActionDone()
        registerObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val email = binding.tiEditEmail.text.toString().trim().lowercase()
            val password = binding.tiEditPassword.text.toString().trim()
            val retypePassword = binding.tiEditRetypePassword.text.toString().trim()

            if (email.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_REGISTER_EMAIL, email)
            }

            if (password.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_REGISTER_PASSWORD, password)
            }

            if (retypePassword.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_REGISTER_RETYPE_PASSWORD, retypePassword)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_REGISTER_EMAIL)?.let { restoredEmail ->
                binding.tiEditEmail.setText(restoredEmail)
            }

            it.getString(SAVE_INSTANCE_REGISTER_PASSWORD)?.let { restoredPassword ->
                binding.tiEditPassword.setText(restoredPassword)
            }

            it.getString(SAVE_INSTANCE_REGISTER_RETYPE_PASSWORD)?.let { restoredRetypePassword ->
                binding.tiEditRetypePassword.setText(restoredRetypePassword)
            }
        }
        super.onViewStateRestored(savedInstanceState)
    }

    private fun textInputsBackgroundColor() = binding.apply {
        tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        tiEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
                tiLayoutPassword.isHelperTextEnabled = false
            }
        }

        tiEditRetypePassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() = binding.apply {
        tiEditEmail.addTextChangedListener {
            tiLayoutEmail.isErrorEnabled = false
            tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        tiEditPassword.addTextChangedListener { inputPassword ->
            tiLayoutPassword.isErrorEnabled = false

            if (tiLayoutPassword.hasFocus()) {
                when (passwordStrength(inputPassword.toString())) {
                    (-1).toByte() -> {
                        tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.red)
                        tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_weak)
                        tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.red)
                        )
                    }
                    (0).toByte() -> {
                        tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_mediocre)
                        tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.orange)
                        )
                    }
                    (1).toByte() -> {
                        tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.green)
                        tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_strong)
                        tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.green)
                        )
                    }
                }
            }
        }

        tiEditRetypePassword.addTextChangedListener {
            tiLayoutRetypePassword.isErrorEnabled = false
            tiLayoutRetypePassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun agreementOnclick() = binding.btnAgreement.setOnClickListener {
        openLink(requireContext(), requireView(), URL_PRIVACY_POLICY)
    }

    private fun loginOnClick() = binding.btnLogin.setOnClickListener {
        findNavController().popBackStack()
    }

    private fun registerOnClick() = binding.btnRegister.setOnClickListener {
        getRegisterInputs()
    }

    private fun registerActionDone() =
        binding.tiEditRetypePassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getRegisterInputs()
            }
            false
        }

    private fun getRegisterInputs() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase()
        val password = binding.tiEditPassword.text.toString().trim()
        val retypePassword = binding.tiEditRetypePassword.text.toString().trim()

        viewModel.checkRegisterInputs(email, password, retypePassword)
    }

    private fun registerObserver() =
        viewModel.registerLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response.status) {
                    Status.LOADING -> showLoadingAnimation()
                    Status.SUCCESS -> {
                        hideLoadingAnimation()
                        Intent(requireContext(), MainActivity::class.java).apply {
                            startActivity(this)
                            requireActivity().finish()
                        }
                    }
                    Status.ERROR -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_INPUT_BLANK_EMAIL) -> {
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.register_error_blank_email)
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_PASSWORD) -> {
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.register_error_blank_password)
                                    binding.tiLayoutPassword.requestFocus()
                                    binding.tiLayoutPassword.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_BLANK_RETYPE_PASSWORD) -> {
                                    binding.tiLayoutRetypePassword.error =
                                        requireContext().getString(R.string.register_error_blank_retype_password)
                                    binding.tiLayoutRetypePassword.requestFocus()
                                    binding.tiLayoutRetypePassword.boxStrokeColor =
                                        ContextCompat.getColor(requireContext(), R.color.red)
                                }
                                it.contains(ERROR_INPUT_EMAIL_INVALID) -> {
                                    binding.tiLayoutEmail.requestFocus()
                                    binding.tiLayoutEmail.error =
                                        requireContext().getString(R.string.register_error_email)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_SHORT) -> {
                                    binding.tiLayoutPassword.requestFocus()
                                    binding.tiLayoutPassword.error =
                                        requireContext().getString(R.string.register_error_password_short)
                                }
                                it.contains(ERROR_INPUT_PASSWORD_NOT_MATCH) -> {
                                    binding.tiLayoutRetypePassword.requestFocus()
                                    binding.tiLayoutRetypePassword.error =
                                        requireContext().getString(R.string.register_error_password_not_match)
                                }
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = showNetworkConnectionError(
                                        requireContext(), requireView()
                                    ) { getRegisterInputs() }
                                }
                                it.contains(ERROR_FIREBASE_403) -> {
                                    snackbar = show403Error(requireContext(), requireView())
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = showFirebaseDeviceBlockedError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS) -> {
                                    snackbar = showFirebaseAuthAccountExists(
                                        requireView(),
                                        requireContext().getString(R.string.register_error_account_exists),
                                        requireContext().getString(R.string.register_btn_login)
                                    ) { findNavController().popBackStack() }
                                }
                                else -> {
                                    snackbar = showNetworkFailureError(
                                        requireContext(),
                                        requireView()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() = binding.apply {
        tiEditEmail.isEnabled = false
        tiEditPassword.isEnabled = false
        tiEditRetypePassword.isEnabled = false
        btnRegister.isClickable = false
        btnRegister.text = null
        cpiRegister.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() = binding.apply {
        cpiRegister.visibility = GONE
        tiEditEmail.isEnabled = true
        tiEditPassword.isEnabled = true
        tiEditRetypePassword.isEnabled = true
        btnRegister.isClickable = true
        btnRegister.text = requireContext().getString(R.string.register_btn_register)
    }
}