package com.xeniac.warrantyroster_manager.warranty_management.presentation.warranties_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeniac.warrantyroster_manager.core.domain.repository.PreferencesRepository
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_SEARCH_RESULT_LIST
import com.xeniac.warrantyroster_manager.util.Constants.ERROR_EMPTY_WARRANTY_LIST
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_LIFETIME
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.util.Event
import com.xeniac.warrantyroster_manager.util.Resource
import com.xeniac.warrantyroster_manager.util.UiText
import com.xeniac.warrantyroster_manager.warranty_management.data.mapper.toWarranty
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Category
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.ListItemType
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.CategoryRepository
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WarrantiesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val warrantyRepository: WarrantyRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _categoriesListLiveData: MutableLiveData<Event<Resource<List<Category>>>> =
        MutableLiveData()
    val categoriesListLiveData: LiveData<Event<Resource<List<Category>>>> = _categoriesListLiveData

    private val _warrantiesListLiveData: MutableLiveData<Event<Resource<MutableList<Warranty>>>> =
        MutableLiveData()
    val warrantiesListLiveData: LiveData<Event<Resource<MutableList<Warranty>>>> =
        _warrantiesListLiveData

    private val _searchWarrantiesLiveData: MutableLiveData<Event<Resource<MutableList<Warranty>>>> =
        MutableLiveData()
    val searchWarrantiesLiveData: LiveData<Event<Resource<MutableList<Warranty>>>> =
        _searchWarrantiesLiveData

    fun getCategoryTitleMapKey(): String {
        val categoryTitleMapKey = preferencesRepository.getCategoryTitleMapKey()
        Timber.i("Category title map key is $categoryTitleMapKey")
        return categoryTitleMapKey
    }

    fun getAllCategoriesList() = viewModelScope.launch {
        safeGetAllCategoriesList()
    }

    private suspend fun safeGetAllCategoriesList() {
        _categoriesListLiveData.postValue(Event(Resource.Loading()))
        try {
            val categoriesList = categoryRepository.getAllCategoriesList()

            if (categoriesList.isEmpty()) {
                Timber.e("safeGetAllCategoriesList Error: $ERROR_EMPTY_CATEGORY_LIST")
                _categoriesListLiveData.postValue(
                    Event(Resource.Error(UiText.DynamicString(ERROR_EMPTY_CATEGORY_LIST)))
                )
            } else {
                _categoriesListLiveData.postValue(Event(Resource.Success(categoriesList)))
                Timber.i("Categories List successfully retrieved.")
            }
        } catch (e: Exception) {
            Timber.e("safeGetAllCategoriesList Exception: ${e.message}")
            _categoriesListLiveData.postValue(Event(Resource.Error(UiText.DynamicString(e.message.toString()))))
        }
    }

    fun getCategoryById(categoryId: String): Category {
        val category = categoryRepository.getCategoryById(categoryId)
        Timber.i("Category found by id is $category")
        return category
    }

    fun getWarrantiesListFromFirestore() = viewModelScope.launch {
        _warrantiesListLiveData.postValue(Event(Resource.Loading()))
        warrantyRepository.getWarrantiesFromFirestore().addSnapshotListener { value, error ->
            error?.let {
                Timber.e("getWarrantiesListFromFirestore Error: ${it.message}")
                _warrantiesListLiveData.postValue(Event(Resource.Error(UiText.DynamicString(it.message.toString()))))
                return@addSnapshotListener
            }

            value?.let {
                if (it.documents.size == 0) {
                    Timber.e("getWarrantiesListFromFirestore Error: $ERROR_EMPTY_WARRANTY_LIST")
                    _warrantiesListLiveData.postValue(
                        Event(Resource.Error(UiText.DynamicString(ERROR_EMPTY_WARRANTY_LIST)))
                    )
                } else {
                    val warrantiesList = mutableListOf<Warranty>()
                    var adIndex = 5

                    it.documents.forEach { document ->
                        val warrantyDto = WarrantyDto(
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
                        warrantiesList.add(warrantyDto.toWarranty())

                        if (warrantiesList.size == adIndex) {
                            adIndex += 6
                            val nativeAd = Warranty(
                                id = "ad_$adIndex",
                                isLifetime = null,
                                categoryId = null,
                                itemType = ListItemType.AD
                            )
                            warrantiesList.add(nativeAd)
                        }
                    }

                    _warrantiesListLiveData.postValue(Event(Resource.Success(warrantiesList)))
                    Timber.i("Warranties List successfully retrieved.")
                }
                return@addSnapshotListener
            }
        }
    }

    fun searchWarrantiesByTitle(searchQuery: String) = viewModelScope.launch {
        warrantiesListLiveData.value?.let { responseEvent ->
            responseEvent.peekContent().let { response ->
                response.data?.let { warrantiesList ->
                    val searchResultList = mutableListOf<Warranty>()

                    warrantiesList.forEach { warranty ->
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
}