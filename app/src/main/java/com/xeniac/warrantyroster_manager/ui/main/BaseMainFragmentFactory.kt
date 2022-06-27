package com.xeniac.warrantyroster_manager.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.xeniac.warrantyroster_manager.ui.main.adapters.WarrantyAdapter
import com.xeniac.warrantyroster_manager.ui.main.fragments.WarrantiesFragment
import javax.inject.Inject

class BaseMainFragmentFactory @Inject constructor(
    private val warrantiesAdapter: WarrantyAdapter
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            WarrantiesFragment::class.java.name -> WarrantiesFragment(warrantiesAdapter)
            else -> super.instantiate(classLoader, className)
        }
    }
}