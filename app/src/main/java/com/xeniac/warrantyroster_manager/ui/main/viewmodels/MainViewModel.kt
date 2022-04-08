package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.di.CategoryTitleMapKey
import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.data.remote.models.ListItemType
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.repositories.MainRepository
import com.xeniac.warrantyroster_manager.utils.Constants.CATEGORIES_ICON
import com.xeniac.warrantyroster_manager.utils.Constants.CATEGORIES_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_LIFETIME
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository,
    @CategoryTitleMapKey private val categoryTitleMapKey: String
) : AndroidViewModel(application) {

    private val _categoriesLiveData:
            MutableLiveData<Event<Resource<List<Category>>>> = MutableLiveData()
    val categoriesLiveData: LiveData<Event<Resource<List<Category>>>> = _categoriesLiveData

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

    init {
        getCategoriesFromFirestore()
    }

    fun getAllCategoryTitles(): List<String> {
        val titleList = mutableListOf<String>()
        categoriesLiveData.value?.let { responseEvent ->
            responseEvent.peekContent().let { response ->
                response.data?.let { categoriesList ->
                    for (category in categoriesList) {
                        titleList.add(category.title[categoryTitleMapKey].toString())
                    }
                }
            }
        }
        return titleList
    }

    fun getCategoryById(categoryId: String): Category? {
        var category: Category? = null
        categoriesLiveData.value?.let { responseEvent ->
            responseEvent.peekContent().let { response ->
                response.data?.let { categoriesList ->
                    category = categoriesList.find { it.id == categoryId }
                }
            }
        }
        return category
    }

    fun getCategoryByTitle(categoryTitle: String): Category? {
        var category: Category? = null
        categoriesLiveData.value?.let { responseEvent ->
            responseEvent.peekContent().let { response ->
                response.data?.let { categoriesList ->
                    category = categoriesList.find { it.title.containsValue(categoryTitle) }
                }
            }
        }
        return category
    }

    fun getCategoriesFromFirestore() = viewModelScope.launch {
        _categoriesLiveData.postValue(Event(Resource.loading()))
        mainRepository.getCategoriesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Timber.e("getCategoriesFromFirestore Error: ${it.message}")
                _categoriesLiveData.postValue(Event(Resource.error(it.message.toString())))
            }

            value?.let {
                if (it.documents.size == 0) {
                    Timber.e("getCategoriesFromFirestore Error: $ERROR_EMPTY_CATEGORY_LIST")
                    _categoriesLiveData.postValue(Event(Resource.error(ERROR_EMPTY_CATEGORY_LIST)))
                } else {
                    val categoriesList = mutableListOf<Category>()

                    for (document in it.documents) {
                        @Suppress("UNCHECKED_CAST")
                        val category = Category(
                            document.id,
                            document.get(CATEGORIES_TITLE) as Map<String, String>,
                            document.get(CATEGORIES_ICON).toString()
                        )
                        categoriesList.add(category)
                    }
                    _categoriesLiveData.postValue(Event(Resource.success(categoriesList)))
                    Timber.i("Categories List successfully retrieved.")
                }
            }
        }
    }

    fun getWarrantiesListFromFirestore() = viewModelScope.launch {
        _warrantiesLiveData.postValue(Event(Resource.loading()))
        mainRepository.getWarrantiesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Timber.e("getWarrantiesListFromFirestore Error: ${it.message}")
                _warrantiesLiveData.postValue(Event(Resource.error(it.message.toString())))
            }

            value?.let {
                if (it.documents.size == 0) {
                    Timber.e("getWarrantiesListFromFirestore Error: $ERROR_EMPTY_WARRANTY_LIST")
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
                            document.get(WARRANTIES_LIFETIME) as Boolean?,
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
                                null, null, null, null,
                                null, null, ListItemType.AD
                            )
                            warrantiesList.add(nativeAd)
                        }
                    }
                    _warrantiesLiveData.postValue(Event(Resource.success(warrantiesList)))
                    Timber.i("Warranties List successfully retrieved.")
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

    private suspend fun safeAddWarrantyToFirestore(warrantyInput: WarrantyInput) {
        _addWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                mainRepository.addWarrantyToFirestore(warrantyInput).await()
                _addWarrantyLiveData.postValue(Event(Resource.success(null)))
                Timber.i("Warranty successfully added.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _addWarrantyLiveData.postValue(Event(Resource.error(ERROR_NETWORK_CONNECTION)))
            }
        } catch (e: Exception) {
            Timber.e("safeAddWarrantyToFirestore Exception: ${e.message}")
            _addWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeDeleteWarrantyFromFirestore(warrantyId: String) {
        _deleteWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                mainRepository.deleteWarrantyFromFirestore(warrantyId).await()
                _deleteWarrantyLiveData.postValue(Event(Resource.success(null)))
                Timber.i("$warrantyId successfully deleted.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _deleteWarrantyLiveData.postValue(Event(Resource.error(ERROR_NETWORK_CONNECTION)))
            }
        } catch (e: Exception) {
            Timber.e("safeDeleteWarrantyFromFirestore Exception: ${e.message}")
            _deleteWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeUpdateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        _updateWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                mainRepository.updateWarrantyInFirestore(warrantyId, warrantyInput).await()
                _updateWarrantyLiveData.postValue(Event(Resource.success(null)))
                Timber.i("Warranty successfully updated.")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _updateWarrantyLiveData.postValue(Event(Resource.error(ERROR_NETWORK_CONNECTION)))
            }
        } catch (e: Exception) {
            Timber.e("safeUpdateWarrantyInFirestore Exception: ${e.message}")
            _updateWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    private suspend fun safeGetUpdatedWarrantyFromFirestore(warrantyId: String) {
        _updatedWarrantyLiveData.postValue(Event(Resource.loading()))
        try {
            if (hasInternetConnection(getApplication<BaseApplication>())) {
                val warrantySnapshot = mainRepository
                    .getUpdatedWarrantyFromFirestore(warrantyId).await()

                val updatedWarranty = Warranty(
                    warrantySnapshot.id,
                    warrantySnapshot.get(WARRANTIES_TITLE).toString(),
                    warrantySnapshot.get(WARRANTIES_BRAND).toString(),
                    warrantySnapshot.get(WARRANTIES_MODEL).toString(),
                    warrantySnapshot.get(WARRANTIES_SERIAL_NUMBER).toString(),
                    warrantySnapshot.get(WARRANTIES_LIFETIME) as Boolean?,
                    warrantySnapshot.get(WARRANTIES_STARTING_DATE).toString(),
                    warrantySnapshot.get(WARRANTIES_EXPIRY_DATE).toString(),
                    warrantySnapshot.get(WARRANTIES_DESCRIPTION).toString(),
                    warrantySnapshot.get(WARRANTIES_CATEGORY_ID).toString()
                )

                _updatedWarrantyLiveData.postValue(Event(Resource.success(updatedWarranty)))
                Timber.i("DocumentSnapshot: $warrantySnapshot")
            } else {
                Timber.e(ERROR_NETWORK_CONNECTION)
                _updatedWarrantyLiveData.postValue(Event(Resource.error(ERROR_NETWORK_CONNECTION)))
            }
        } catch (e: Exception) {
            Timber.e("safeGetUpdatedWarrantyFromFirestore Exception: ${e.message}")
            _updatedWarrantyLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }
}