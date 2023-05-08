package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.settings.presentation.change_email.ChangeEmailFragment
import com.xeniac.warrantyroster_manager.settings.presentation.change_password.ChangePasswordFragment
import com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts.LinkedAccountsFragment
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragment
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            SettingsFragment::class.java.name -> SettingsFragment(null)
            ChangeEmailFragment::class.java.name -> ChangeEmailFragment(null)
            ChangePasswordFragment::class.java.name -> ChangePasswordFragment(null)
            LinkedAccountsFragment::class.java.name -> LinkedAccountsFragment(firebaseAuth)
            else -> super.instantiate(classLoader, className)
        }
    }
}