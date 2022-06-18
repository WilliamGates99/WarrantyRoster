package com.xeniac.warrantyroster_manager.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.xeniac.warrantyroster_manager.BaseApplication
import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.repositories.MainRepository
import com.xeniac.warrantyroster_manager.repositories.PreferencesRepository
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_NETWORK_CONNECTION
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_BLANK_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_INPUT_INVALID_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.DateHelper.isStartingDateValid
import com.xeniac.warrantyroster_manager.utils.Event
import com.xeniac.warrantyroster_manager.utils.NetworkHelper.hasInternetConnection
import com.xeniac.warrantyroster_manager.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository,
    private val preferencesRepository: PreferencesRepository
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
        viewModelScope.launch {
            categoriesLiveData.value?.let { responseEvent ->
                responseEvent.peekContent().let { response ->
                    response.data?.let { categoriesList ->
                        for (category in categoriesList) {
                            titleList.add(category.title[preferencesRepository.getCategoryTitleMapKey()].toString())
                        }
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
        try {
            val categoriesList = mainRepository.getCategoriesFromFirestore()
            _categoriesLiveData.postValue(Event(Resource.success(categoriesList)))
            Timber.i("Categories List successfully retrieved.")
        } catch (e: Exception) {
            Timber.e("getCategoriesFromFirestore Error: ${e.message}")
            _categoriesLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    fun getWarrantiesListFromFirestore() = viewModelScope.launch {
        _warrantiesLiveData.postValue(Event(Resource.loading()))
        try {
            val warrantiesList = mainRepository.getWarrantiesFromFirestore()
            _warrantiesLiveData.postValue(Event(Resource.success(warrantiesList)))
            Timber.i("Warranties List successfully retrieved.")
        } catch (e: Exception) {
            Timber.e("getWarrantiesListFromFirestore Error: ${e.message}")
            _warrantiesLiveData.postValue(Event(Resource.error(e.message.toString())))
        }
    }

    fun checkAddWarrantyInputs(
        title: String, brand: String?, model: String?, serialNumber: String?,
        isLifetime: Boolean, startingDateInput: String?, expiryDateInput: String?,
        description: String?, categoryId: String,
        selectedStartingDateInMillis: Long, selectedExpiryDateInMillis: Long
    ) {
        if (title.isBlank()) {
            _addWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_TITLE)))
            return
        }

        if (startingDateInput.isNullOrBlank()) {
            _addWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_STARTING_DATE)))
            return
        }

        if (!isLifetime && expiryDateInput.isNullOrBlank()) {
            _addWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_EXPIRY_DATE)))
            return
        }

        if (!isLifetime &&
            !isStartingDateValid(selectedStartingDateInMillis, selectedExpiryDateInMillis)
        ) {
            _addWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_INVALID_STARTING_DATE)))
            return
        }

        val warrantyInput = WarrantyInput(
            title,
            brand,
            model,
            serialNumber,
            isLifetime,
            startingDateInput,
            expiryDateInput,
            description,
            categoryId,
            Firebase.auth.currentUser?.uid.toString()
        )

        addWarrantyToFirestore(warrantyInput)
    }

    fun checkEditWarrantyInputs(
        warrantyId: String, title: String, brand: String?, model: String?, serialNumber: String?,
        isLifetime: Boolean, startingDateInput: String?, expiryDateInput: String?,
        description: String?, categoryId: String,
        selectedStartingDateInMillis: Long, selectedExpiryDateInMillis: Long
    ) {
        if (title.isBlank()) {
            _updateWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_TITLE)))
            return
        }

        if (startingDateInput.isNullOrBlank()) {
            _updateWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_STARTING_DATE)))
            return
        }

        if (!isLifetime && expiryDateInput.isNullOrBlank()) {
            _updateWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_BLANK_EXPIRY_DATE)))
            return
        }

        if (!isLifetime &&
            !isStartingDateValid(selectedStartingDateInMillis, selectedExpiryDateInMillis)
        ) {
            _updateWarrantyLiveData.postValue(Event(Resource.error(ERROR_INPUT_INVALID_STARTING_DATE)))
            return
        }

        val warrantyInput = WarrantyInput(
            title,
            brand,
            model,
            serialNumber,
            isLifetime,
            startingDateInput,
            expiryDateInput,
            description,
            categoryId,
            Firebase.auth.currentUser?.uid.toString()
        )

        updateWarrantyInFirestore(warrantyId, warrantyInput)
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
                mainRepository.addWarrantyToFirestore(warrantyInput)
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
                mainRepository.deleteWarrantyFromFirestore(warrantyId)
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
                mainRepository.updateWarrantyInFirestore(warrantyId, warrantyInput)
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
                val updatedWarranty = mainRepository.getUpdatedWarrantyFromFirestore(warrantyId)
                _updatedWarrantyLiveData.postValue(Event(Resource.success(updatedWarranty)))
                Timber.i("Updated warranty successfully retrieved.")
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