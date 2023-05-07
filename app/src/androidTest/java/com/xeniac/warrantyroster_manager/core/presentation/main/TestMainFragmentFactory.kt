package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragment
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsViewModel
import javax.inject.Inject

class TestMainFragmentFactory @Inject constructor() : FragmentFactory() {

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
            SettingsFragment::class.java.name -> SettingsFragment(
                SettingsViewModel(
                    fakeUserRepository,
                    FakePreferencesRepository()
                )
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}