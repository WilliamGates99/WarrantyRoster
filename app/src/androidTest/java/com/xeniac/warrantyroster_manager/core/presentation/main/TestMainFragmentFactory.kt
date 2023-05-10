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
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput
import com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty.AddWarrantyFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty.AddWarrantyViewModel
import com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty.EditWarrantyFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty.EditWarrantyViewModel
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantiesFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantiesViewModel
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list.WarrantyAdapter
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details.WarrantyDetailsFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details.WarrantyDetailsViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

class TestMainFragmentFactory @Inject constructor(
    private val warrantyAdapter: WarrantyAdapter,
    private val imageLoader: ImageLoader,
    private val decimalFormat: DecimalFormat,
    private val dateFormat: SimpleDateFormat,
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
        addTestWarrantyToWarrantyRepository()

        return when (className) {
            WarrantiesFragment::class.java.name -> WarrantiesFragment(
                warrantyAdapter,
                WarrantiesViewModel(
                    fakeCategoryRepository,
                    fakeWarrantyRepository,
                    FakePreferencesRepository()
                )
            )
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
            WarrantyDetailsFragment::class.java.name -> WarrantyDetailsFragment(
                imageLoader,
                decimalFormat,
                dateFormat,
                WarrantyDetailsViewModel(
                    fakeCategoryRepository,
                    fakeWarrantyRepository
                )
            )
            EditWarrantyFragment::class.java.name -> EditWarrantyFragment(
                imageLoader,
                decimalFormat,
                dateFormat,
                EditWarrantyViewModel(
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

    private fun addTestWarrantyToWarrantyRepository() {
        val warrantyInput = WarrantyInput(
            title = "Warranty Title",
            brand = "Warranty Brand",
            model = "Warranty Model",
            serialNumber = "WARRANTY_SERIAL",
            description = "This is warranty description.",
            startingDate = "2022-07-13",
            expiryDate = "2050-07-13",
            uuid = "1"
        )

        fakeWarrantyRepository.addWarranty(
            warrantyInput = warrantyInput,
            warrantyId = "1"
        )
    }

    fun deleteTestWarrantyFromWarrantyRepository(warrantyId: String) {
        fakeWarrantyRepository.deleteWarranty(warrantyId)
    }
}