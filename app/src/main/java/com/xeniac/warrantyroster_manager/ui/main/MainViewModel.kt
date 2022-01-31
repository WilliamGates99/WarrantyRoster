package com.xeniac.warrantyroster_manager.ui.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.WarrantyRosterApplication
import com.xeniac.warrantyroster_manager.models.Category
import com.xeniac.warrantyroster_manager.models.ListItemType
import com.xeniac.warrantyroster_manager.models.Warranty
import com.xeniac.warrantyroster_manager.models.WarrantyInput
import com.xeniac.warrantyroster_manager.repositories.WarrantyRepository
import com.xeniac.warrantyroster_manager.utils.CategoryHelper.getCategoryTitleMapKey
import com.xeniac.warrantyroster_manager.utils.Constants
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel(
    application: Application,
    private val warrantyRepository: WarrantyRepository
) : AndroidViewModel(application) {

    val warrantiesLiveData: MutableLiveData<Resource<MutableList<Warranty>>> = MutableLiveData()
    val addWarrantyLiveData: MutableLiveData<Resource<Nothing>> = MutableLiveData()

    private val TAG = "MainViewModel"

    fun seedCategories() = viewModelScope.launch {
        try {
            val seedPrefs = getApplication<WarrantyRosterApplication>()
                .getSharedPreferences(Constants.PREFERENCE_DB_SEED, Context.MODE_PRIVATE)
            val isEnUsSeeded = seedPrefs.getBoolean(Constants.PREFERENCE_EN_US_KEY, false)

            //TODO add isFaIRSeeded after adding persian
            if (!isEnUsSeeded) {
                deleteAllCategories()
                insertAllCategories(getCategoriesFromFirestore())

                if (countItems() == 21) {
                    Log.i(TAG, "categories successfully seeded to DB.")
                    getApplication<WarrantyRosterApplication>().getSharedPreferences(
                        Constants.PREFERENCE_DB_SEED, Context.MODE_PRIVATE
                    ).edit().apply {
                        putBoolean(Constants.PREFERENCE_EN_US_KEY, true)
                        apply()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

    fun getAllCategoryTitles(): List<String> {
        val titleList = mutableListOf<String>()
        for (category in warrantyRepository.getAllCategories()) {
            titleList.add(category.title[getCategoryTitleMapKey(getApplication<WarrantyRosterApplication>())].toString())
        }
        return titleList
    }

    fun getCategoryById(categoryId: String) = warrantyRepository.getCategoryById(categoryId)

    fun getWarrantiesListFromFirestore() = viewModelScope.launch {
        warrantiesLiveData.postValue(Resource.Loading())
        warrantyRepository.getWarrantiesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Log.e(TAG, "Error: ${it.message}")
                warrantiesLiveData.postValue(Resource.Error(it.message.toString()))
            }

            value?.let {
                Log.i(TAG, "Warranties List successfully retrieved.")

                if (it.documents.size == 0) {
                    warrantiesLiveData.postValue(Resource.Error("Warranty list is empty"))
                } else {
                    val warrantiesList = mutableListOf<Warranty>()
                    var adIndex = 5

                    for (document in it.documents) {
                        val warranty = Warranty(
                            document.id,
                            document.get(WARRANTIES_TITLE).toString(),
                            document.get(WARRANTIES_BRAND).toString(),
                            document.get(WARRANTIES_MODEL).toString(),
                            document.get(WARRANTIES_SERIAL_NUMBER).toString(),
                            document.get(WARRANTIES_STARTING_DATE).toString(),
                            document.get(WARRANTIES_EXPIRY_DATE).toString(),
                            document.get(WARRANTIES_DESCRIPTION).toString(),
                            document.get(WARRANTIES_CATEGORY_ID).toString(),
                            ListItemType.WARRANTY
                        )
                        warrantiesList.add(warranty)

                        if (warrantiesList.size == adIndex) {
                            adIndex += 6
                            val nativeAd = Warranty(
                                null, null, null, null,
                                null, null, null,
                                null, null, ListItemType.AD
                            )
                            warrantiesList.add(nativeAd)
                        }
                    }
                    warrantiesLiveData.postValue(Resource.Success(warrantiesList))
                }
            }
        }
    }

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) = viewModelScope.launch {
        safeAddWarrantyToFirestore(warrantyInput)
    }

    private fun deleteAllCategories() = viewModelScope.launch {
        warrantyRepository.deleteAllCategories()
    }

    private fun insertAllCategories(categoriesList: List<Category>) = viewModelScope.launch {
        warrantyRepository.insertAllCategories(categoriesList)
    }

    private fun countItems() = warrantyRepository.countItems()

    private fun getCategoriesFromFirestore(): List<Category> {
        val categoriesList = mutableListOf<Category>()
        viewModelScope.launch {
            try {
                val categoriesQuery = warrantyRepository.getCategoriesFromFirestore().await()
                Log.i(TAG, "Categories successfully retrieved.")

                for (document in categoriesQuery.documents) {
                    @Suppress("UNCHECKED_CAST")
                    document?.let {
                        val id = it.id
                        val title = it.get(Constants.CATEGORIES_TITLE) as Map<String, String>
                        val icon = it.get(Constants.CATEGORIES_ICON).toString()
                        categoriesList.add(Category(id, title, icon))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
            }
        }
        return categoriesList.toList()
    }

    private suspend fun safeAddWarrantyToFirestore(warrantyInput: WarrantyInput) {
        addWarrantyLiveData.postValue(Resource.Loading())
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