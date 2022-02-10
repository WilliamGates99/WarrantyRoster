package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xeniac.warrantyroster_manager.repositories.MainRepository

class MainViewModelProviderFactory(
    private val application: Application,
    private val mainRepository: MainRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application, mainRepository) as T
    }
}