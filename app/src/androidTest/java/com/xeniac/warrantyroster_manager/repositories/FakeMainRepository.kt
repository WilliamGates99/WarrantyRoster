package com.xeniac.warrantyroster_manager.repositories

import androidx.lifecycle.MutableLiveData
import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput

class FakeMainRepository : MainRepository {

    private val categories = mutableListOf<Category>()
    private val warranties = mutableListOf<Warranty>()

    private val observableCategories = MutableLiveData<List<Category>>(categories)
    private val observableWarranties = MutableLiveData<List<Warranty>>(warranties)

    private var shouldReturnNetworkError = false

    fun addWarranty(warrantyInput: WarrantyInput) {
        val warranty = warrantyInput.let {
            Warranty(
                "1", it.title, it.brand, it.model, it.serialNumber, it.lifetime,
                it.startingDate, it.expiryDate, it.description, it.categoryId
            )
        }
        warranties.add(warranty)
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshLiveData() {
        observableCategories.postValue(categories)
        observableWarranties.postValue(warranties)
    }

    override fun getCategoriesFromFirestore(): MutableList<Category> {
        if (shouldReturnNetworkError) {
            throw Exception()
        }

        if (categories.size == 0) {
            throw Exception()
        }

        return categories
    }

    override fun getWarrantiesFromFirestore(): MutableList<Warranty> {
        if (shouldReturnNetworkError) {
            throw Exception()
        }

        if (warranties.size == 0) {
            throw Exception()
        }

        return warranties
    }

    override suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            addWarranty(warrantyInput)
            refreshLiveData()
        }
    }

    override suspend fun deleteWarrantyFromFirestore(warrantyId: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            if (!warranties.remove(warranties.find { it.id == warrantyId })) {
                throw Exception()
            }
            refreshLiveData()
        }
    }

    override suspend fun updateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            warranties.removeAt(0)
            addWarranty(warrantyInput)
            refreshLiveData()
        }
    }

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            return warranties.find { it.id == warrantyId } ?: throw Exception()
        }
    }
}