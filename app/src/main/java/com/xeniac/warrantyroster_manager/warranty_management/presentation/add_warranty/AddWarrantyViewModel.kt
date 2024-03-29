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

    private val _addWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val addWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _addWarrantyLiveData

    fun getCategoryTitleMapKey(): String {
        val categoryTitleMapKey = preferencesRepository.getCategoryTitleMapKey()
        Timber.i("Category title map key is $categoryTitleMapKey")
        return categoryTitleMapKey
    }

    fun getAllCategoryTitles(titleMapKey: String): List<String> {
        val allCategoryTitlesList = categoryRepository.getAllCategoryTitlesList(titleMapKey)
        Timber.i("All category titles are $allCategoryTitlesList")
        return allCategoryTitlesList
    }

    fun getCategoryById(categoryId: String): Category {
        val category = categoryRepository.getCategoryById(categoryId)
        Timber.i("Category found by id is $category")
        return category
    }

    fun getCategoryByTitle(categoryTitle: String): Category {
        val category = categoryRepository.getCategoryByTitle(categoryTitle)
        Timber.i("Category found by title is $category")
        return category
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

        val isExpiryDateInputBlank = !isLifetime && expiryDateInput.isNullOrBlank()
        if (isExpiryDateInputBlank) {
            _addWarrantyLiveData.postValue(
                Event(Resource.Error(UiText.DynamicString(ERROR_INPUT_BLANK_EXPIRY_DATE)))
            )
            return
        }

        val isStartingDateInvalid = !isLifetime && !DateHelper.isStartingDateValid(
            selectedStartingDateInMillis,
            selectedExpiryDateInMillis
        )
        if (isStartingDateInvalid) {
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