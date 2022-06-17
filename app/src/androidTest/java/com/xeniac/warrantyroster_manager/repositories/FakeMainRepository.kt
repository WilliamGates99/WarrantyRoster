package com.xeniac.warrantyroster_manager.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
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

    override fun getCategoriesFromFirestore(): Query {
        TODO("Not yet implemented")
//        if (shouldReturnNetworkError) {
//            throw Exception()
//        } else {
//            return Firebase.firestore.collection("test")
//        }
    }

    override fun getWarrantiesFromFirestore(): Query {
        TODO("Not yet implemented")
//        if (shouldReturnNetworkError) {
//            throw Exception()
//        } else {
//            return Firebase.firestore.collection("test")
//        }
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
            for (warranty in warranties) {
                if (warranty.id == warrantyId) {
                    warranties.remove(warranty)
                } else {
                    throw Exception()
                }
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

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Any {
        TODO("Not yet implemented")
    }
}