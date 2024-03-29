package com.xeniac.warrantyroster_manager.core.presentation.landing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.authentication.presentation.auth.AuthFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.auth.AuthViewModel
import com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password.ForgotPwFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password.ForgotPwSentFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password.ForgotPwViewModel
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginViewModel
import com.xeniac.warrantyroster_manager.authentication.presentation.register.RegisterFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.register.RegisterViewModel
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding.OnBoardingFragment
import com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding.OnBoardingViewModel
import java.text.DecimalFormat
import javax.inject.Inject

class TestLandingFragmentFactory @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val decimalFormat: DecimalFormat
) : FragmentFactory() {

    private val email = "email@test.com"
    private val password = "password"
    private val isEmailVerified = false

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fakeUserRepository = FakeUserRepository()

        fakeUserRepository.addUser(
            email = email,
            password = password,
            isEmailVerified = isEmailVerified
        )

        return when (className) {
            OnBoardingFragment::class.java.name -> OnBoardingFragment(
                OnBoardingViewModel(FakePreferencesRepository())
            )
            AuthFragment::class.java.name -> AuthFragment(
                AuthViewModel(FakePreferencesRepository())
            )
            LoginFragment::class.java.name -> LoginFragment(
                firebaseAuth,
                LoginViewModel(
                    fakeUserRepository,
                    FakePreferencesRepository()
                )
            )
            RegisterFragment::class.java.name -> RegisterFragment(
                firebaseAuth,
                RegisterViewModel(
                    fakeUserRepository,
                    FakePreferencesRepository()
                )
            )
            ForgotPwFragment::class.java.name -> ForgotPwFragment(
                ForgotPwViewModel(
                    fakeUserRepository,
                    SavedStateHandle()
                )
            )
            ForgotPwSentFragment::class.java.name -> ForgotPwSentFragment(
                decimalFormat,
                ForgotPwViewModel(
                    fakeUserRepository,
                    SavedStateHandle()
                )
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}