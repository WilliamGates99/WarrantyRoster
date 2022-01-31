package com.xeniac.warrantyroster_manager.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xeniac.warrantyroster_manager.repositories.WarrantyRepository

class MainViewModelProviderFactory(
    private val application: Application,
    private val warrantyRepository: WarrantyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, warrantyRepository) as T
    }
}