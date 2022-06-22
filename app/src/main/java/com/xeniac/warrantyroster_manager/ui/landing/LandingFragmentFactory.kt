package com.xeniac.warrantyroster_manager.ui.landing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.xeniac.warrantyroster_manager.ui.landing.fragments.LoginFragment
import com.xeniac.warrantyroster_manager.ui.landing.fragments.RegisterFragment
import javax.inject.Inject

class LandingFragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            LoginFragment::class.java.name -> LoginFragment()
            RegisterFragment::class.java.name -> RegisterFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}