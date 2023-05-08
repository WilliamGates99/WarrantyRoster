package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import coil.ImageLoader
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
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_TAG_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.warranty_management.data.repository.FakeCategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.data.repository.FakeWarrantyRepository
import com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty.AddWarrantyFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty.AddWarrantyViewModel
import java.text.DecimalFormat
import javax.inject.Inject

class TestMainFragmentFactory @Inject constructor(
    private val imageLoader: ImageLoader,
    private val decimalFormat: DecimalFormat,
    private val firebaseAuth: FirebaseAuth
) : FragmentFactory() {

    private val fakeUserRepository = FakeUserRepository()
    private val fakeCategoryRepository = FakeCategoryRepository()
    private val fakeWarrantyRepository = FakeWarrantyRepository()

    private val email = "email@test.com"
    private val password = "password"
    private val isEmailVerified = false

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        addTestUserToUserRepository()
        addTestCategoriesToCategoryRepository()

        return when (className) {
            AddWarrantyFragment::class.java.name -> AddWarrantyFragment(
                imageLoader,
                decimalFormat,
                AddWarrantyViewModel(
                    fakeUserRepository,
                    fakeCategoryRepository,
                    fakeWarrantyRepository,
                    FakePreferencesRepository()
                )
            )
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

    private fun addTestUserToUserRepository() {
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
    }

    private fun addTestCategoriesToCategoryRepository() {
        for (i in 1..5) {
            fakeCategoryRepository.addCategory(
                id = i.toString(),
                title = mapOf(Pair(LOCALE_TAG_ENGLISH_UNITED_STATES, "Title $i")),
                icon = "$i.svg"
            )
        }
    }
}