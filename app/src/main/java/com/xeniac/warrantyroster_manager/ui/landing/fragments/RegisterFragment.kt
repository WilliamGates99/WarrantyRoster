package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyroster_manager.ui.landing.LandingActivity
import com.xeniac.warrantyroster_manager.ui.landing.LandingViewModel
import com.xeniac.warrantyroster_manager.ui.main.MainActivity
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PRIVACY_POLICY
import com.xeniac.warrantyroster_manager.utils.Resource

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LandingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        viewModel = (activity as LandingActivity).viewModel

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
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

        binding.tiEditPassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutPassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
                binding.tiLayoutPassword.isHelperTextEnabled = false
            }
        }

        binding.tiEditRetypePassword.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutRetypePassword.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiEditEmail.addTextChangedListener {
            binding.tiLayoutEmail.isErrorEnabled = false
            binding.tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }

        binding.tiEditPassword.addTextChangedListener { inputPassword ->
            if (binding.tiLayoutPassword.hasFocus()) {
                when (passwordStrength(inputPassword.toString())) {
                    (-1).toByte() -> {
                        binding.tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.red)
                        binding.tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_weak)
                        binding.tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.red)
                        )
                    }
                    (0).toByte() -> {
                        binding.tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.orange)
                        binding.tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_mediocre)
                        binding.tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.orange)
                        )
                    }
                    (1).toByte() -> {
                        binding.tiLayoutPassword.boxStrokeColor =
                            ContextCompat.getColor(requireContext(), R.color.green)
                        binding.tiLayoutPassword.helperText =
                            getString(R.string.register_helper_password_strong)
                        binding.tiLayoutPassword.setHelperTextColor(
                            ContextCompat.getColorStateList(requireContext(), R.color.green)
                        )
                    }
                }
            }
        }

        binding.tiEditRetypePassword.addTextChangedListener {
            binding.tiLayoutRetypePassword.isErrorEnabled = false
            binding.tiLayoutRetypePassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.blue)
        }
    }

    private fun agreementOnclick() = binding.btnAgreement.setOnClickListener {
        Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIVACY_POLICY)).apply {
            resolveActivity(requireContext().packageManager)?.let {
                startActivity(this)
            } ?: Snackbar.make(
                binding.root,
                getString(R.string.intent_error_app_not_found),
                LENGTH_LONG
            ).show()
        }
    }

    private fun loginOnClick() = binding.btnLogin.setOnClickListener {
        requireActivity().onBackPressed()
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
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase()
        val password = binding.tiEditPassword.text.toString().trim()
        val retypePassword = binding.tiEditRetypePassword.text.toString().trim()

        if (email.isBlank()) {
            binding.tiLayoutEmail.requestFocus()
            binding.tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (password.isBlank()) {
            binding.tiLayoutPassword.requestFocus()
            binding.tiLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (retypePassword.isBlank()) {
            binding.tiLayoutRetypePassword.requestFocus()
            binding.tiLayoutRetypePassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiLayoutEmail.requestFocus()
                binding.tiLayoutEmail.error =
                    requireContext().getString(R.string.register_error_email)
            } else if (passwordStrength(password) == (-1).toByte()) {
                binding.tiLayoutPassword.requestFocus()
                binding.tiLayoutPassword.error =
                    requireContext().getString(R.string.register_error_password_short)
            } else if (!isRetypePasswordValid(password, retypePassword)) {
                binding.tiLayoutRetypePassword.requestFocus()
                binding.tiLayoutRetypePassword.error =
                    requireContext().getString(R.string.register_error_password)
            } else {
                registerViaEmail(email, password)
            }
        }
    }

    private fun registerViaEmail(email: String, password: String) =
        viewModel.registerViaEmail(email, password)

    private fun registerObserver() =
        viewModel.registerLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> showLoadingAnimation()
                is Resource.Success -> {
                    hideLoadingAnimation()
                    Intent(requireContext(), MainActivity::class.java).apply {
                        startActivity(this)
                        requireActivity().finish()
                    }
                }
                is Resource.Error -> {
                    hideLoadingAnimation()
                    response.message?.let {
                        when {
                            it.contains("Unable to connect to the internet") -> {
                                Snackbar.make(
                                    binding.root,
                                    requireContext().getString(R.string.network_error_connection),
                                    LENGTH_LONG
                                ).apply {
                                    setAction(requireContext().getString(R.string.network_error_retry)) { getRegisterInputs() }
                                    show()
                                }
                            }
                            it.contains("The email address is already in use by another account") -> {
                                Snackbar.make(
                                    binding.root,
                                    requireContext().getString(R.string.register_error_account_exists),
                                    LENGTH_LONG
                                ).apply {
                                    setAction(requireContext().getString(R.string.register_btn_login)) { requireActivity().onBackPressed() }
                                    show()
                                }
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

            if (viewModel.registerLiveData.value != null) {
                viewModel.registerLiveData.value = null
            }
        }

    private fun showLoadingAnimation() {
        binding.tiEditEmail.isEnabled = false
        binding.tiEditPassword.isEnabled = false
        binding.tiEditRetypePassword.isEnabled = false
        binding.btnRegister.isClickable = false
        binding.btnRegister.text = null
        binding.cpiRegister.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiRegister.visibility = GONE
        binding.tiEditEmail.isEnabled = true
        binding.tiEditPassword.isEnabled = true
        binding.tiEditRetypePassword.isEnabled = true
        binding.btnRegister.isClickable = true
        binding.btnRegister.text =
            requireContext().getString(R.string.register_btn_register)
    }

    private fun passwordStrength(password: String): Byte {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=(.*[\\W])*).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return when {
            password.length < 6 -> -1
            password.length < 8 -> 0
            else -> if (passwordMatcher.matches(password)) 1 else 0
        }
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isRetypePasswordValid(password: String, retypePassword: String): Boolean =
        password == retypePassword
}