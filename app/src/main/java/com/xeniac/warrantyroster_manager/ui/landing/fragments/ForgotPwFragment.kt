package com.xeniac.warrantyroster_manager.ui.landing.fragments

import android.content.Context
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
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentForgotPwBinding
import com.xeniac.warrantyroster_manager.models.Resource
import com.xeniac.warrantyroster_manager.ui.landing.LandingViewModel
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_FIREBASE_DEVICE_BLOCKED
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_403
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_TIMER_IS_NOT_ZERO
import com.xeniac.warrantyroster_manager.utils.Constants.SAVE_INSTANCE_FORGOT_PW_EMAIL
import com.xeniac.warrantyroster_manager.utils.UserHelper.isEmailValid
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPwFragment : Fragment(R.layout.fragment_forgot_pw) {

    private var _binding: FragmentForgotPwBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LandingViewModel

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPwBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[LandingViewModel::class.java]

        textInputsBackgroundColor()
        textInputsStrokeColor()
        returnOnClick()
        sendOnClick()
        sendActionDone()
        forgotPwObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        _binding?.let {
            val email = binding.tiEditEmail.text.toString().trim().lowercase()

            if (email.isNotBlank()) {
                outState.putString(SAVE_INSTANCE_FORGOT_PW_EMAIL, email)
            }
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.getString(SAVE_INSTANCE_FORGOT_PW_EMAIL)?.let { restoredEmail ->
                binding.tiEditEmail.setText(restoredEmail)
            }
        }
        super.onViewStateRestored(savedInstanceState)
    }

    private fun textInputsBackgroundColor() =
        binding.tiEditEmail.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.background)
            } else {
                binding.tiLayoutEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.grayLight)
            }
        }

    private fun textInputsStrokeColor() = binding.tiEditEmail.addTextChangedListener {
        binding.tiLayoutEmail.isErrorEnabled = false
        binding.tiLayoutEmail.boxStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.blue)
    }

    private fun returnOnClick() = binding.btnReturn.setOnClickListener {
        requireActivity().onBackPressed()
    }

    private fun sendOnClick() = binding.btnSend.setOnClickListener {
        getResetPasswordInput()
    }

    private fun sendActionDone() =
        binding.tiEditEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getResetPasswordInput()
            }
            false
        }

    private fun getResetPasswordInput() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.applicationWindowToken, 0)

        val email = binding.tiEditEmail.text.toString().trim().lowercase()

        if (email.isBlank()) {
            binding.tiLayoutEmail.requestFocus()
            binding.tiLayoutEmail.boxStrokeColor =
                ContextCompat.getColor(requireContext(), R.color.red)
        } else {
            if (!isEmailValid(email)) {
                binding.tiLayoutEmail.requestFocus()
                binding.tiLayoutEmail.error =
                    requireContext().getString(R.string.forgot_pw_error_email)
            } else {
                sendResetPasswordEmail(email)
            }
        }
    }

    private fun sendResetPasswordEmail(email: String) = viewModel.sendResetPasswordEmail(email)

    private fun forgotPwObserver() =
        viewModel.forgotPwLiveData.observe(viewLifecycleOwner) { responseEvent ->
            responseEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> showLoadingAnimation()
                    is Resource.Success -> {
                        hideLoadingAnimation()

                        response.data?.let { email ->
                            val action = ForgotPwFragmentDirections
                                .actionForgotPasswordFragmentToForgotPwSentFragment(email)
                            findNavController().navigate(action)
                        }
                    }
                    is Resource.Error -> {
                        hideLoadingAnimation()
                        response.message?.let {
                            when {
                                it.contains(ERROR_NETWORK_CONNECTION) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.error_network_connection),
                                        LENGTH_INDEFINITE
                                    ).apply {
                                        setAction(requireContext().getString(R.string.error_btn_retry)) { getResetPasswordInput() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_NETWORK_403) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.error_firebase_403),
                                        LENGTH_INDEFINITE
                                    ).apply {
                                        setAction(requireContext().getString(R.string.error_btn_confirm)) { dismiss() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_FIREBASE_DEVICE_BLOCKED) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.error_firebase_device_blocked),
                                        LENGTH_INDEFINITE
                                    ).apply {
                                        setAction(requireContext().getString(R.string.error_btn_confirm)) { dismiss() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND) -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.forgot_pw_error_not_found),
                                        LENGTH_INDEFINITE
                                    ).apply {
                                        setAction(requireContext().getString(R.string.error_btn_confirm)) { dismiss() }
                                        show()
                                    }
                                }
                                it.contains(ERROR_TIMER_IS_NOT_ZERO) -> {
                                    val seconds = viewModel.timerInMillis / 1000
                                    if (seconds <= 1L) {
                                        snackbar = Snackbar.make(
                                            binding.root,
                                            requireContext().getString(
                                                R.string.forgot_pw_error_timer_is_not_zero_one,
                                                seconds
                                            ),
                                            LENGTH_LONG
                                        ).apply {
                                            show()
                                        }
                                    } else {
                                        snackbar = Snackbar.make(
                                            binding.root,
                                            requireContext().getString(
                                                R.string.forgot_pw_error_timer_is_not_zero_other,
                                                seconds
                                            ),
                                            LENGTH_LONG
                                        ).apply {
                                            show()
                                        }
                                    }
                                }
                                else -> {
                                    snackbar = Snackbar.make(
                                        binding.root,
                                        requireContext().getString(R.string.error_network_failure),
                                        LENGTH_LONG
                                    ).apply {
                                        show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun showLoadingAnimation() {
        binding.tiEditEmail.isEnabled = false
        binding.btnSend.isClickable = false
        binding.btnSend.text = null
        binding.cpiSend.visibility = VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.cpiSend.visibility = GONE
        binding.tiEditEmail.isEnabled = true
        binding.btnSend.isClickable = true
        binding.btnSend.text =
            requireContext().getString(R.string.login_btn_login)
    }
}