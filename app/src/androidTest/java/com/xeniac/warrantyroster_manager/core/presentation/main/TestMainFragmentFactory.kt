package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.data.repository.FakePreferencesRepository
import com.xeniac.warrantyroster_manager.core.data.repository.FakeUserRepository
import com.xeniac.warrantyroster_manager.settings.presentation.change_email.ChangeEmailFragment
import com.xeniac.warrantyroster_manager.settings.presentation.change_email.ChangeEmailViewModel
import com.xeniac.warrantyroster_manager.settings.presentation.change_password.ChangePasswordFragment
import com.xeniac.warrantyroster_manager.settings.presentation.change_password.ChangePasswordViewModel
import com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts.LinkedAccountsFragment
import com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts.LinkedAccountsViewModel
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragment
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsViewModel
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import javax.inject.Inject

class TestMainFragmentFactory @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FragmentFactory() {

    private val email = "email@test.com"
    private val password = "password"
    private val isEmailVerified = false

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fakeUserRepository = FakeUserRepository()

        fakeUserRepository.addUser(
            email = email,
            password = password,
            isEmailVerified = isEmailVerified,
            providerIds = mutableListOf(
                FIREBASE_AUTH_PROVIDER_ID_GOOGLE,
                FIREBASE_AUTH_PROVIDER_ID_TWITTER,
                FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
            )
        )

        return when (className) {
            SettingsFragment::class.java.name -> SettingsFragment(
                SettingsViewModel(
                    fakeUserRepository,
                    FakePreferencesRepository()
                )
            )
            ChangeEmailFragment::class.java.name -> ChangeEmailFragment(
                ChangeEmailViewModel(fakeUserRepository)
            )
            ChangePasswordFragment::class.java.name -> ChangePasswordFragment(
                ChangePasswordViewModel(fakeUserRepository)
            )
            LinkedAccountsFragment::class.java.name -> LinkedAccountsFragment(
                firebaseAuth,
                LinkedAccountsViewModel(
                    fakeUserRepository,
                    FakePreferencesRepository()
                )
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}