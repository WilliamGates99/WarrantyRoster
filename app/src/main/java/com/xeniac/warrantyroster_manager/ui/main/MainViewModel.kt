package com.xeniac.warrantyroster_manager.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.WarrantyRosterApplication
import com.xeniac.warrantyroster_manager.models.WarrantyInput
import com.xeniac.warrantyroster_manager.repositories.WarrantyRepository
import com.xeniac.warrantyroster_manager.utils.CategoryHelper.getCategoryTitleMapKey
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel(
    application: Application,
    private val warrantyRepository: WarrantyRepository
) : AndroidViewModel(application) {

    val addWarrantyLiveData: MutableLiveData<Resource<Nothing>> = MutableLiveData()

    private val TAG = "MainViewModel"

    fun getAllCategoryTitles(): List<String> {
        val titleList = mutableListOf<String>()
        for (category in warrantyRepository.getAllCategories()) {
            titleList.add(category.title[getCategoryTitleMapKey(getApplication<WarrantyRosterApplication>())].toString())
        }
        return titleList
    }

    fun getCategoryById(categoryId: String) = warrantyRepository.getCategoryById(categoryId)

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) = viewModelScope.launch {
        safeAddWarrantyToFirestore(warrantyInput)
    }

    private suspend fun safeAddWarrantyToFirestore(warrantyInput: WarrantyInput) {
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                warrantyRepository.addWarrantyToFirestore(warrantyInput).await()
                addWarrantyLiveData.postValue(Resource.Success(null))
                Log.i(TAG, "Warranty successfully added.")
            } else {
                Log.e(TAG, "Unable to connect to the internet")
                addWarrantyLiveData.postValue(
                    Resource.Error("Unable to connect to the internet")
                )
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception: ${t.message}")
            addWarrantyLiveData.postValue(Resource.Error(t.message.toString()))
        }
    }
}