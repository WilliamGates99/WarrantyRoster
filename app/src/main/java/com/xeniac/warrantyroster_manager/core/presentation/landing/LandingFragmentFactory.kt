package com.xeniac.warrantyroster_manager.core.presentation.landing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.authentication.presentation.auth.AuthFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password.ForgotPwFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.forgot_password.ForgotPwSentFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.login.LoginFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.register.RegisterFragment
import com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding.OnBoardingFragment
import java.text.DecimalFormat
import javax.inject.Inject

class LandingFragmentFactory @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val decimalFormat: DecimalFormat
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            OnBoardingFragment::class.java.name -> OnBoardingFragment(null)
            AuthFragment::class.java.name -> AuthFragment(null)
            LoginFragment::class.java.name -> LoginFragment(firebaseAuth)
            RegisterFragment::class.java.name -> RegisterFragment(firebaseAuth)
            ForgotPwFragment::class.java.name -> ForgotPwFragment(null)
            ForgotPwSentFragment::class.java.name -> ForgotPwSentFragment(decimalFormat)
            else -> super.instantiate(classLoader, className)
        }
    }
}