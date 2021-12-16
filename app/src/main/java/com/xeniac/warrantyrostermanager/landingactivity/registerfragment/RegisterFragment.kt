package com.xeniac.warrantyrostermanager.landingactivity.registerfragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyrostermanager.Constants
import com.xeniac.warrantyrostermanager.NetworkHelper
import com.xeniac.warrantyrostermanager.R
import com.xeniac.warrantyrostermanager.databinding.FragmentRegisterBinding
import com.xeniac.warrantyrostermanager.mainactivity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()

        textInputsBackgroundColor()
        textInputsStrokeColor()
        agreementOnclick()
        loginOnClick()
        registerOnClick()
        registerActionDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun textInputsBackgroundColor() {
        binding.tiRegisterEditEmail.setOnFocusChangeListener { _, focused ->
            if (focused) {
                binding.tiRegisterLayoutEmail.setBoxBackgroundColorResource(R.color.background)
            } else {
                binding.tiRegisterLayoutEmail.setBoxBackgroundColorResource(R.color.grayLight)
            }
        }

        binding.tiRegisterEditPassword.setOnFocusChangeListener { _, focused ->
            if (focused) {
                binding.tiRegisterLayoutPassword.setBoxBackgroundColorResource(R.color.background)
            } else {
                binding.tiRegisterLayoutPassword.setBoxBackgroundColorResource(R.color.grayLight)
                binding.tiRegisterLayoutPassword.isHelperTextEnabled = false
            }
        }

        binding.tiRegisterEditRetypePassword.setOnFocusChangeListener { _, focused ->
            if (focused) {
                binding.tiRegisterLayoutRetypePassword.setBoxBackgroundColorResource(R.color.background)
            } else {
                binding.tiRegisterLayoutRetypePassword.setBoxBackgroundColorResource(R.color.grayLight)
            }
        }
    }

    private fun textInputsStrokeColor() {
        binding.tiRegisterEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputEmail: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiRegisterLayoutEmail.isErrorEnabled = false
                binding.tiRegisterLayoutEmail.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiRegisterEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputPassword: CharSequence?, start: Int, before: Int, count: Int
            ) {
                if (binding.tiRegisterLayoutPassword.hasFocus()) {
                    when (passwordStrength(inputPassword.toString())) {
                        (-1).toByte() -> {
                            binding.tiRegisterLayoutPassword.helperText =
                                getString(R.string.register_helper_password_weak)
                            binding.tiRegisterLayoutPassword.setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.red)
                                )
                            )
                            binding.tiRegisterLayoutPassword.boxStrokeColor =
                                ContextCompat.getColor(requireContext(), R.color.red)
                        }
                        (0).toByte() -> {
                            binding.tiRegisterLayoutPassword.helperText =
                                getString(R.string.register_helper_password_mediocre)
                            binding.tiRegisterLayoutPassword.setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.orange)
                                )
                            )
                            binding.tiRegisterLayoutPassword.boxStrokeColor =
                                ContextCompat.getColor(requireContext(), R.color.orange)
                        }
                        (1).toByte() -> {
                            binding.tiRegisterLayoutPassword.helperText =
                                getString(R.string.register_helper_password_strong)
                            binding.tiRegisterLayoutPassword.setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.green)
                                )
                            )
                            binding.tiRegisterLayoutPassword.boxStrokeColor =
                                ContextCompat.getColor(requireContext(), R.color.green)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tiRegisterEditRetypePassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                inputRetypePassword: CharSequence?, start: Int, before: Int, count: Int
            ) {
                binding.tiRegisterLayoutRetypePassword.isErrorEnabled = false
                binding.tiRegisterLayoutRetypePassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), R.color.blue)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun agreementOnclick() {
        binding.btnRegisterAgreement.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_PRIVACY_POLICY)).apply {
                resolveActivity(requireContext().packageManager)?.let {
                    startActivity(this)
                } ?: Snackbar.make(
                    binding.root,
                    getString(R.string.intent_error_app_not_found),
                    LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loginOnClick() {
        binding.btnRegisterLogin.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun registerOnClick() {
        binding.btnRegisterRegister.setOnClickListener {
            registerViaEmail()
        }
    }

    private fun registerActionDone() {
        binding.tiRegisterEditRetypePassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViaEmail()
            }
            false
        }
    }

    private fun registerViaEmail() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        if (NetworkHelper.hasNetworkAccess(context)) {
            getRegisterInputs()
        } else {
            hideLoadingAnimation()
            Snackbar.make(
                binding.root,
                requireContext().getString(R.string.network_error_connection),
                LENGTH_INDEFINITE
            ).apply {
                setAction(requireContext().getString(R.string.network_error_retry)) { registerViaEmail() }
                show()
            }
        }
    }

    private fun getRegisterInputs() {
        val email = binding.tiRegisterEditEmail.text.toString().trim().lowercase()
        val password = binding.tiRegisterEditPassword.text.toString().trim()
        val retypePassword = binding.tiRegisterEditRetypePassword.text.toString().trim()

        if (email.isBlank()) {
            binding.tiRegisterLayoutEmail.requestFocus()
            binding.tiRegisterLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (password.isBlank()) {
            binding.tiRegisterLayoutPassword.requestFocus()
            binding.tiRegisterLayoutPassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else if (retypePassword.isBlank()) {
            binding.tiRegisterLayoutRetypePassword.requestFocus()
            binding.tiRegisterLayoutRetypePassword.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiRegisterLayoutEmail.requestFocus()
                binding.tiRegisterLayoutEmail.error =
                    requireContext().getString(R.string.register_error_email)
            } else if (passwordStrength(password) == (-1).toByte()) {
                binding.tiRegisterLayoutPassword.requestFocus()
                binding.tiRegisterLayoutPassword.error =
                    requireContext().getString(R.string.register_error_password_short)
            } else if (!isRetypePasswordValid(password, retypePassword)) {
                binding.tiRegisterLayoutRetypePassword.requestFocus()
                binding.tiRegisterLayoutRetypePassword.error =
                    requireContext().getString(R.string.register_error_password)
            } else {
                registerViaEmailAuth(email, password)
            }
        }
    }

    private fun registerViaEmailAuth(email: String, password: String) {
        showLoadingAnimation()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await().apply {
                    user?.let {
                        Log.i("registerViaEmail", "${it.email} registered successfully.")
                        it.sendEmailVerification()

                        it.getIdToken(false).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val idToken = task.result.token

                                requireContext()
                                    .getSharedPreferences(
                                        Constants.PREFERENCE_LOGIN,
                                        Context.MODE_PRIVATE
                                    )
                                    .edit().apply {
                                        putString(Constants.PREFERENCE_USER_TOKEN_KEY, idToken)
                                        putBoolean(Constants.PREFERENCE_IS_LOGGED_IN_KEY, true)
                                        apply()
                                    }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            hideLoadingAnimation()
                            Intent(requireContext(), MainActivity::class.java).apply {
                                startActivity(this)
                                requireActivity().finish()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("registerViaEmail", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    hideLoadingAnimation()
                    if (e.toString().contains(
                            "The email address is already in use by another account"
                        )
                    ) {
                        Snackbar.make(
                            binding.root,
                            requireContext().getString(R.string.register_error_account_exists),
                            LENGTH_INDEFINITE
                        ).apply {
                            setAction(requireContext().getString(R.string.register_btn_login)) { requireActivity().onBackPressed() }
                            show()
                        }
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
        binding.tiRegisterEditEmail.isEnabled = false
        binding.tiRegisterEditPassword.isEnabled = false
        binding.tiRegisterEditRetypePassword.isEnabled = false
        binding.btnRegisterRegister.isClickable = false
        binding.btnRegisterRegister.text = null
        binding.cpiRegister.visibility = View.VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiRegister.visibility = View.GONE
        binding.tiRegisterEditEmail.isEnabled = true
        binding.tiRegisterEditPassword.isEnabled = true
        binding.tiRegisterEditRetypePassword.isEnabled = true
        binding.btnRegisterRegister.isClickable = true
        binding.btnRegisterRegister.text =
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

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isRetypePasswordValid(password: String, retypePassword: String): Boolean {
        return password == retypePassword
    }
}