package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranty_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.CategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WarrantyDetailsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val warrantyRepository: WarrantyRepository
) : ViewModel() {

    private val _categoryByIdLiveData: MutableLiveData<Event<Resource<Category>>> =
        MutableLiveData()
    val categoryByIdLiveData: LiveData<Event<Resource<Category>>> = _categoryByIdLiveData

    private val _deleteWarrantyLiveData: MutableLiveData<Event<Resource<Nothing>>> =
        MutableLiveData()
    val deleteWarrantyLiveData: LiveData<Event<Resource<Nothing>>> = _deleteWarrantyLiveData

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