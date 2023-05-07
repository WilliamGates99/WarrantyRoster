package com.xeniac.warrantyroster_manager.core.presentation.landing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.xeniac.warrantyroster_manager.authentication.presentation.auth.AuthFragment
import com.xeniac.warrantyroster_manager.authentication.presentation.auth.AuthViewModel
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import javax.inject.Inject

class TestLandingFragmentFactory @Inject constructor() : FragmentFactory() {

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
            AuthFragment::class.java.name -> AuthFragment(
                AuthViewModel(FakePreferencesRepository())
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}