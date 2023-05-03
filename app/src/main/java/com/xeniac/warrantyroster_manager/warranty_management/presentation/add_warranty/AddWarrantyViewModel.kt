package com.xeniac.warrantyroster_manager.warranty_management.presentation.add_warranty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.core.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_BLANK_TITLE
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_INPUT_INVALID_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.DateHelper
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.CategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddWarrantyViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val warrantyRepository: WarrantyRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _categoryTitleMapKeyLiveData: MutableLiveData<Event<Resource<String>>> =
        MutableLiveData()
    val categoryTitleMapKeyLiveData: LiveData<Event<Resource<String>>> =
        _categoryTitleMapKeyLiveData

    private val _allCategoryTitlesLiveData: MutableLiveData<Event<Resource<List<String>>>> =
        MutableLiveData()
    val allCategoryTitlesLiveData: LiveData<Event<Resource<List<String>>>> =
        _allCategoryTitlesLiveData

    private val _categoryByIdLiveData: MutableLiveData<Event<Resource<Category>>> =
        MutableLiveData()
    val categoryByIdLiveData: LiveData<Event<Resource<Category>>> = _categoryByIdLiveData

    private val _categoryByTitleLiveData: MutableLiveData<Event<Resource<Category>>> =
        MutableLiveData()
    val categoryByTitleLiveData: LiveData<Event<Resource<Category>>> = _categoryByTitleLiveData

    private val _addWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val addWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _addWarrantyLiveData

    fun getCategoryTitleMapKey() = viewModelScope.launch {
        safeGetCategoryTitleMapKey()
    }

    private suspend fun safeGetCategoryTitleMapKey() {
        _categoryTitleMapKeyLiveData.postValue(Event(Resource.Loading()))
        try {
            val categoryTitleMapKey = preferencesRepository.getCategoryTitleMapKey()
            _categoryTitleMapKeyLiveData.postValue(Event(Resource.Success(categoryTitleMapKey)))
            Timber.i("Category title map key is $categoryTitleMapKey")
        } catch (e: Exception) {
            Timber.e("safeGetCategoryTitleMapKey Exception: ${e.message}")
            _categoryTitleMapKeyLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getAllCategoryTitles(titleMapKey: String) = viewModelScope.launch {
        safeGetAllCategoryTitles(titleMapKey)
    }

    private suspend fun safeGetAllCategoryTitles(titleMapKey: String) {
        _allCategoryTitlesLiveData.postValue(Event(Resource.Loading()))
        try {
            val allCategoryTitlesList = categoryRepository.getAllCategoryTitlesList(titleMapKey)
            _allCategoryTitlesLiveData.postValue(Event(allCategoryTitlesList))
            Timber.i("All category titles are $allCategoryTitlesList")
        } catch (e: Exception) {
            Timber.e("safeGetAllCategoryTitles Exception: ${e.message}")
            _allCategoryTitlesLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getCategoryById(categoryId: String) = viewModelScope.launch {
        safeGetCategoryById(categoryId)
    }

    private suspend fun safeGetCategoryById(categoryId: String) {
        _categoryByIdLiveData.postValue(Event(Resource.Loading()))
        try {
            val category = categoryRepository.getCategoryById(categoryId)
            _categoryByIdLiveData.postValue(Event(category))
            Timber.i("Category found by id is $category")
        } catch (e: Exception) {
            Timber.e("safeGetCategoryById Exception: ${e.message}")
            _categoryByIdLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getCategoryByTitle(categoryTitle: String) = viewModelScope.launch {
        safeGetCategoryByTitle(categoryTitle)
    }

    private suspend fun safeGetCategoryByTitle(categoryTitle: String) {
        _categoryByTitleLiveData.postValue(Event(Resource.Loading()))
        try {
            val category = categoryRepository.getCategoryByTitle(categoryTitle)
            _categoryByTitleLiveData.postValue(Event(category))
            Timber.i("Category found by title is $category")
        } catch (e: Exception) {
            Timber.e("safeGetCategoryByTitle Exception: ${e.message}")
            _categoryByTitleLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
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
            !DateHelper.isStartingDateValid(
                selectedStartingDateInMillis,
                selectedExpiryDateInMillis
            )
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
}