package com.xeniac.warrantyroster_manager.warranty_management.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.Category
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.ListItemType
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyInput
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.CATEGORIES_ICON
import com.xeniac.warrantyroster_manager.util.Constants.CATEGORIES_TITLE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_SEARCH_RESULT_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_TITLE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_INVALID_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_ENGLISH_GREAT_BRITAIN
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_ENGLISH_UNITED_STATES
import com.xeniac.warrantyroster_manager.util.Constants.LOCALE_PERSIAN_IRAN
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_LIFETIME
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.util.DateHelper.isStartingDateValid
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WarrantyViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val warrantyRepository: WarrantyRepository
) : ViewModel() {

    private val _categoriesLiveData:
            MutableLiveData<Event<Resource<List<Category>>>> = MutableLiveData()
    val categoriesLiveData: LiveData<Event<Resource<List<Category>>>> = _categoriesLiveData

    private val _warrantiesLiveData:
            MutableLiveData<Event<Resource<MutableList<Warranty>>>> = MutableLiveData()
    val warrantiesLiveData: LiveData<Event<Resource<MutableList<Warranty>>>> = _warrantiesLiveData

    private val _searchWarrantiesLiveData:
            MutableLiveData<Event<Resource<MutableList<Warranty>>>> = MutableLiveData()
    val searchWarrantiesLiveData:
            LiveData<Event<Resource<MutableList<Warranty>>>> = _searchWarrantiesLiveData

    private val _addWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val addWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _addWarrantyLiveData

    private val _deleteWarrantyLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val deleteWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _deleteWarrantyLiveData

    private val _updateWarrantyLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val updateWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _updateWarrantyLiveData

    private val _updatedWarrantyLiveData:
            MutableLiveData<Event<Resource<Warranty>>> = MutableLiveData()
    val updatedWarrantyLiveData: LiveData<Event<Resource<Warranty>>> = _updatedWarrantyLiveData

    fun getCategoryTitleMapKey(): String = safeGetCategoryTitleMapKey()

    private fun safeGetCategoryTitleMapKey(): String {
        val localeList = AppCompatDelegate.getApplicationLocales()

        return if (localeList.isEmpty) {
            Timber.i("Locale list is Empty.")
            LOCALE_ENGLISH_UNITED_STATES
        } else {
            val localeString = localeList[0].toString()
            Timber.i("Current language is $localeString")

            when (localeString) {
                "en_US" -> LOCALE_ENGLISH_UNITED_STATES
                "en_GB" -> LOCALE_ENGLISH_GREAT_BRITAIN
                "fa_IR" -> LOCALE_PERSIAN_IRAN
                else -> LOCALE_ENGLISH_UNITED_STATES
            }
        }
    }

    fun getAllCategoryTitles(): List<String> {
        val titleList = mutableListOf<String>()
        viewModelScope.launch {
            categoriesLiveData.value?.let { responseEvent ->
                responseEvent.peekContent().let { response ->
                    response.data?.let { categoriesList ->
                        for (category in categoriesList) {
                            titleList.add(category.title[getCategoryTitleMapKey()].toString())
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
        _categoriesLiveData.postValue(Event(Resource.Loading()))
        warrantyRepository.getCategoriesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Timber.e("getCategoriesFromFirestore Error: ${it.message}")
                _categoriesLiveData.postValue(Event(Resource.Error(UiText.DynamicString(it.message.toString()))))
                return@addSnapshotListener
            }

            value?.let {
                if (it.documents.size == 0) {
                    Timber.e("getCategoriesFromFirestore Error: $ERROR_EMPTY_CATEGORY_LIST")
                    _categoriesLiveData.postValue(
                        Event(Resource.Error(UiText.DynamicString(ERROR_EMPTY_CATEGORY_LIST)))
                    )
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

                    _categoriesLiveData.postValue(Event(Resource.Success(categoriesList)))
                    Timber.i("Categories List successfully retrieved.")
                    return@addSnapshotListener
                }
            }
        }
    }

    fun getWarrantiesListFromFirestore() = viewModelScope.launch {
        _warrantiesLiveData.postValue(Event(Resource.Loading()))
        warrantyRepository.getWarrantiesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Timber.e("getWarrantiesListFromFirestore Error: ${it.message}")
                _warrantiesLiveData.postValue(Event(Resource.Error(UiText.DynamicString(it.message.toString()))))
                return@addSnapshotListener
            }

            value?.let {
                if (it.documents.size == 0) {
                    Timber.e("getWarrantiesListFromFirestore Error: $ERROR_EMPTY_WARRANTY_LIST")
                    _warrantiesLiveData.postValue(
                        Event(Resource.Error(UiText.DynamicString(ERROR_EMPTY_WARRANTY_LIST)))
                    )
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
                                id = adIndex.toString(),
                                isLifetime = null,
                                categoryId = null,
                                itemType = ListItemType.AD
                            )
                            warrantiesList.add(nativeAd)
                        }
                    }

                    _warrantiesLiveData.postValue(Event(Resource.Success(warrantiesList)))
                    Timber.i("Warranties List successfully retrieved.")
                }
                return@addSnapshotListener
            }
        }
    }

    fun searchWarrantiesByTitle(searchQuery: String) = viewModelScope.launch {
        warrantiesLiveData.value?.let { responseEvent ->
            responseEvent.peekContent().let { response ->
                response.data?.let { warrantiesList ->
                    val searchResultList = mutableListOf<Warranty>()

                    for (warranty in warrantiesList) {
                        warranty.title?.let {
                            if (it.lowercase().contains(searchQuery)) {
                                searchResultList.add(warranty)
                            }
                        }
                    }

                    if (searchResultList.size == 0) {
                        Timber.e("searchWarrantiesByTitle Error: $ERROR_EMPTY_SEARCH_RESULT_LIST")
                        _searchWarrantiesLiveData.postValue(
                            Event(Resource.Error(UiText.DynamicString(ERROR_EMPTY_SEARCH_RESULT_LIST)))
                        )
                    } else {
                        _searchWarrantiesLiveData.postValue(Event(Resource.Success(searchResultList)))
                        Timber.i("searchWarrantiesByTitle was successful.")
                    }
                }
            }
        }
    }

    fun validateAddWarrantyInputs(
        title: String, brand: String?, model: String?, serialNumber: String?,
        isLifetime: Boolean, startingDateInput: String?, expiryDateInput: String?,
        description: String?, categoryId: String,
        selectedStartingDateInMillis: Long, selectedExpiryDateInMillis: Long
    ) = viewModelScope.launch {
        safeValidateAddWarrantyInputs(
            title, brand, model, serialNumber, isLifetime, startingDateInput, expiryDateInput,
            description, categoryId, selectedStartingDateInMillis, selectedExpiryDateInMillis
        )
    }

    private fun safeValidateAddWarrantyInputs(
        title: String, brand: String?, model: String?, serialNumber: String?,
        isLifetime: Boolean, startingDateInput: String?, expiryDateInput: String?,
        description: String?, categoryId: String,
        selectedStartingDateInMillis: Long, selectedExpiryDateInMillis: Long
    ) {
        if (title.isBlank()) {
            _addWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_TITLE)))
            )
            return
        }

        if (startingDateInput.isNullOrBlank()) {
            _addWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_STARTING_DATE)))
            )
            return
        }

        if (!isLifetime && expiryDateInput.isNullOrBlank()) {
            _addWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EXPIRY_DATE)))
            )
            return
        }

        if (!isLifetime &&
            !isStartingDateValid(selectedStartingDateInMillis, selectedExpiryDateInMillis)
        ) {
            _addWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_INVALID_STARTING_DATE)))
            )
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
            userRepository.getCurrentUserUid()
        )

        addWarrantyToFirestore(warrantyInput)
    }

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) = viewModelScope.launch {
        safeAddWarrantyToFirestore(warrantyInput)
    }

    private suspend fun safeAddWarrantyToFirestore(warrantyInput: WarrantyInput) {
        _addWarrantyLiveData.postValue(Event(Resource.Loading()))
        try {
            warrantyRepository.addWarrantyToFirestore(warrantyInput)
            _addWarrantyLiveData.postValue(Event(Resource.Success()))
            Timber.i("Warranty successfully added.")
        } catch (e: Exception) {
            Timber.e("safeAddWarrantyToFirestore Exception: ${e.message}")
            _addWarrantyLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun validateEditWarrantyInputs(
        warrantyId: String, title: String, brand: String?, model: String?, serialNumber: String?,
        isLifetime: Boolean, startingDateInput: String?, expiryDateInput: String?,
        description: String?, categoryId: String,
        selectedStartingDateInMillis: Long, selectedExpiryDateInMillis: Long
    ) = viewModelScope.launch {
        safeValidateEditWarrantyInputs(
            warrantyId, title, brand, model, serialNumber, isLifetime,
            startingDateInput, expiryDateInput, description, categoryId,
            selectedStartingDateInMillis, selectedExpiryDateInMillis
        )
    }

    private fun safeValidateEditWarrantyInputs(
        warrantyId: String, title: String, brand: String?, model: String?, serialNumber: String?,
        isLifetime: Boolean, startingDateInput: String?, expiryDateInput: String?,
        description: String?, categoryId: String,
        selectedStartingDateInMillis: Long, selectedExpiryDateInMillis: Long
    ) {
        if (title.isBlank()) {
            _updateWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_TITLE)))
            )
            return
        }

        if (startingDateInput.isNullOrBlank()) {
            _updateWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_STARTING_DATE)))
            )
            return
        }

        if (!isLifetime && expiryDateInput.isNullOrBlank()) {
            _updateWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EXPIRY_DATE)))
            )
            return
        }

        if (!isLifetime &&
            !isStartingDateValid(selectedStartingDateInMillis, selectedExpiryDateInMillis)
        ) {
            _updateWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_INVALID_STARTING_DATE)))
            )
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
            userRepository.getCurrentUserUid()
        )

        updateWarrantyInFirestore(warrantyId, warrantyInput)
    }

    fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput) =
        viewModelScope.launch {
            safeUpdateWarrantyInFirestore(warrantyId, warrantyInput)
        }

    private suspend fun safeUpdateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        _updateWarrantyLiveData.postValue(Event(Resource.Loading()))
        try {
            warrantyRepository.updateWarrantyInFirestore(warrantyId, warrantyInput)
            _updateWarrantyLiveData.postValue(Event(Resource.Success()))
            Timber.i("Warranty successfully updated.")
        } catch (e: Exception) {
            Timber.e("safeUpdateWarrantyInFirestore Exception: ${e.message}")
            _updateWarrantyLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getUpdatedWarrantyFromFirestore(warrantyId: String) = viewModelScope.launch {
        safeGetUpdatedWarrantyFromFirestore(warrantyId)
    }

    private suspend fun safeGetUpdatedWarrantyFromFirestore(warrantyId: String) {
        _updatedWarrantyLiveData.postValue(Event(Resource.Loading()))
        try {
            val updatedWarranty = warrantyRepository.getUpdatedWarrantyFromFirestore(warrantyId)
            _updatedWarrantyLiveData.postValue(Event(Resource.Success(updatedWarranty)))
            Timber.i("Updated warranty successfully retrieved.")
        } catch (e: Exception) {
            Timber.e("safeGetUpdatedWarrantyFromFirestore Exception: ${e.message}")
            _updatedWarrantyLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun deleteWarrantyFromFirestore(warrantyId: String) = viewModelScope.launch {
        safeDeleteWarrantyFromFirestore(warrantyId)
    }

    private suspend fun safeDeleteWarrantyFromFirestore(warrantyId: String) {
        _deleteWarrantyLiveData.postValue(Event(Resource.Loading()))
        try {
            warrantyRepository.deleteWarrantyFromFirestore(warrantyId)
            _deleteWarrantyLiveData.postValue(Event(Resource.Success()))
            Timber.i("$warrantyId successfully deleted.")
        } catch (e: Exception) {
            Timber.e("safeDeleteWarrantyFromFirestore Exception: ${e.message}")
            _deleteWarrantyLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }
}