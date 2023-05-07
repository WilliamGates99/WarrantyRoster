package com.xeniac.warrantyroster_manager.core.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.xeniac.warrantyroster_manager.settings.presentation.settings.SettingsFragment
import javax.inject.Inject

class MainFragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            SettingsFragment::class.java.name -> SettingsFragment(null)
            else -> super.instantiate(classLoader, className)
        }
    }
}