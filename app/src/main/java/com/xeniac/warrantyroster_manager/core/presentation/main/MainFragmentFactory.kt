package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import coil.ImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.settings.presentation.change_email.ChangeEmailFragment
import com.xeniac.warrantyroster_manager.settings.presentation.change_password.ChangePasswordFragment
import com.xeniac.warrantyroster_manager.settings.presentation.linked_accounts.LinkedAccountsFragment
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty.AddWarrantyFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty.EditWarrantyFragment
import com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details.WarrantyDetailsFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

class MainFragmentFactory @Inject constructor(
    private val imageLoader: ImageLoader,
    private val decimalFormat: DecimalFormat,
    private val dateFormat: SimpleDateFormat,
    private val firebaseAuth: FirebaseAuth
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            AddWarrantyFragment::class.java.name -> AddWarrantyFragment(imageLoader, decimalFormat)
            WarrantyDetailsFragment::class.java.name -> WarrantyDetailsFragment(
                imageLoader, decimalFormat, dateFormat
            )
            EditWarrantyFragment::class.java.name -> EditWarrantyFragment(
                imageLoader, decimalFormat, dateFormat
            )
            SettingsFragment::class.java.name -> SettingsFragment(null)
            ChangeEmailFragment::class.java.name -> ChangeEmailFragment(null)
            ChangePasswordFragment::class.java.name -> ChangePasswordFragment(null)
            LinkedAccountsFragment::class.java.name -> LinkedAccountsFragment(firebaseAuth)
            else -> super.instantiate(classLoader, className)
        }
    }
}