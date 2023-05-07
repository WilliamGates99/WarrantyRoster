package com.xeniac.warrantyroster_manager.core.presentation.landing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.xeniac.warrantyroster_manager.authentication.presentation.auth.AuthFragment
import javax.inject.Inject

class LandingFragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            AuthFragment::class.java.name -> AuthFragment(null)
            else -> super.instantiate(classLoader, className)
        }
    }
}