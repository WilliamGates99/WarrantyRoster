package com.xeniac.warrantyroster_manager.warranty_management.presentation.edit_warranty

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
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.CategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditWarrantyViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val warrantyRepository: WarrantyRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _updateWarrantyLiveData:
            MutableLiveData<Event<Resource<Nothing>>> = MutableLiveData()
    val updateWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _updateWarrantyLiveData

    private val _updatedWarrantyLiveData:
            MutableLiveData<Event<Resource<Warranty>>> = MutableLiveData()
    val updatedWarrantyLiveData: LiveData<Event<Resource<Warranty>>> = _updatedWarrantyLiveData

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
            !DateHelper.isStartingDateValid(
                selectedStartingDateInMillis,
                selectedExpiryDateInMillis
            )
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
}