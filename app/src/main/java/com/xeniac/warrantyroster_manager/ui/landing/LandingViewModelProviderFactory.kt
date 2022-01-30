package com.xeniac.warrantyroster_manager.ui.landing

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xeniac.warrantyroster_manager.repositories.UserRepository

class LandingViewModelProviderFactory(
    private val application: Application,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LandingViewModel(application, userRepository) as T
    }
}