package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel(
    application: Application,
    private val warrantyRepository: WarrantyRepository
) : AndroidViewModel(application) {

    private val _warrantiesLiveData:
            MutableLiveData<Event<Resource<MutableList<Warranty>>>> = MutableLiveData()
    val warrantiesLiveData: LiveData<Event<Resource<MutableList<Warranty>>>> = _warrantiesLiveData

    private val _addWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val addWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _addWarrantyLiveData

    private val _deleteWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val deleteWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _deleteWarrantyLiveData

    private val _updateWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val updateWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _updateWarrantyLiveData

    private val _updatedWarrantyLiveData: MutableLiveData<Event<Resource<Warranty>>> =
        MutableLiveData()
    val updatedWarrantyLiveData: LiveData<Event<Resource<Warranty>>> = _updatedWarrantyLiveData

    companion object {
        private const val TAG = "MainViewModel"
    }

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
            Log.e(TAG, "SeedCategories Exception: ${e.message}")
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
        _warrantiesLiveData.postValue(Event(Resource.loading()))
        warrantyRepository.getWarrantiesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Log.e(TAG, "GetWarrantiesListFromFirestore Error: ${it.message}")
                _warrantiesLiveData.postValue(Event(Resource.error(it.message.toString())))
            }

            value?.let {
                Log.i(TAG, "Warranties List successfully retrieved.")

                if (it.documents.size == 0) {
                    _warrantiesLiveData.postValue(Event(Resource.error(ERROR_EMPTY_WARRANTY_LIST)))
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
                            document.get(WARRANTIES_CATEGORY_ID).toString()
                        )
                        warrantiesList.add(warranty)

                        if (warrantiesList.size == adIndex) {
                            adIndex += 6
                            val nativeAd = Warranty(
                                adIndex.toString(), null, null, null,
                                null, null, null,
                                null, null, ListItemType.AD
                            )
                            warrantiesList.add(nativeAd)
                        }
                    }
                    _warrantiesLiveData.postValue(Event(Resource.success(warrantiesList)))
                }
            }
        }
    }

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) = viewModelScope.launch {
        safeAddWarrantyToFirestore(warrantyInput)
    }

    fun deleteWarrantyFromFirestore(warrantyId: String) = viewModelScope.launch {
        safeDeleteWarrantyFromFirestore(warrantyId)
    }

    fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput) =
        viewModelScope.launch {
            safeUpdateWarrantyInFirestore(warrantyId, warrantyInput)
        }

    fun getUpdatedWarrantyFromFirestore(warrantyId: String) = viewModelScope.launch {
        safeGetUpdatedWarrantyFromFirestore(warrantyId)
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
                Log.e(TAG, "GetCategoriesFromFirestore Exception: ${e.message}")
            }
        }
        return categoriesList.toList()
    }

    private suspend fun safeAddWarrantyToFirestore(warrantyInput: WarrantyInput) {
        _addWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                warrantyRepository.addWarrantyToFirestore(warrantyInput).await()
                _addWarrantyLiveData.postValue(Event(Resource.success(null)))
                Log.i(TAG, "Warranty successfully added.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _addWarrantyLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeAddWarrantyToFirestore Exception: ${e.message}")
            _addWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeDeleteWarrantyFromFirestore(warrantyId: String) {
        _deleteWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                warrantyRepository.deleteWarrantyFromFirestore(warrantyId).await()
                _deleteWarrantyLiveData.postValue(Event(Resource.success(null)))
                Log.i(TAG, "$warrantyId successfully deleted.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _deleteWarrantyLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeDeleteWarrantyFromFirestore Exception: ${e.message}")
            _deleteWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeUpdateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        _updateWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                warrantyRepository.updateWarrantyInFirestore(warrantyId, warrantyInput).await()
                _updateWarrantyLiveData.postValue(Event(Resource.success(null)))
                Log.i(TAG, "Warranty successfully updated.")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _updateWarrantyLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeUpdateWarrantyInFirestore Exception: ${e.message}")
            _updateWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeGetUpdatedWarrantyFromFirestore(warrantyId: String) {
        _updatedWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<WarrantyRosterApplication>())) {
                val warrantySnapshot = warrantyRepository
                    .getUpdatedWarrantyFromFirestore(warrantyId).await()

                val updatedWarranty = Warranty(
                    warrantySnapshot.id,
                    warrantySnapshot.get(WARRANTIES_TITLE).toString(),
                    warrantySnapshot.get(WARRANTIES_BRAND).toString(),
                    warrantySnapshot.get(WARRANTIES_MODEL).toString(),
                    warrantySnapshot.get(WARRANTIES_SERIAL_NUMBER).toString(),
                    warrantySnapshot.get(WARRANTIES_STARTING_DATE).toString(),
                    warrantySnapshot.get(WARRANTIES_EXPIRY_DATE).toString(),
                    warrantySnapshot.get(WARRANTIES_DESCRIPTION).toString(),
                    warrantySnapshot.get(WARRANTIES_CATEGORY_ID).toString()
                )

                _updatedWarrantyLiveData.postValue(Event(Resource.success(updatedWarranty)))
                Log.i(TAG, "DocumentSnapshot: $warrantySnapshot")
            } else {
                Log.e(TAG, ERROR_NETWORK_CONNECTION)
                _updatedWarrantyLiveData.postValue(
                    Event(Resource.error(ERROR_NETWORK_CONNECTION))
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "SafeGetUpdatedWarrantyFromFirestore Exception: ${e.message}")
            _updatedWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }
}