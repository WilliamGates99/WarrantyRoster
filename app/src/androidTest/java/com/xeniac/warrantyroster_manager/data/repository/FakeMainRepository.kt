package com.xeniac.warrantyroster_manager.data.repository

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.domain.repository.MainRepository

class FakeMainRepository : MainRepository {

    //    private val categories = mutableListOf<Category>()
    private val warranties = mutableListOf<Warranty>()

    private var shouldReturnNetworkError = false

    fun addWarranty(warrantyInput: WarrantyInput, warrantyId: String = "1") {
        val warranty = warrantyInput.let {
            Warranty(
                warrantyId, it.title, it.brand, it.model, it.serialNumber, it.lifetime,
                it.startingDate, it.expiryDate, it.description, it.categoryId
            )
        }
        warranties.add(warranty)
    }

    /*
    fun addCategory(id: String, title: Map<String, String>, icon: String) {
        categories.add(Category(id, title, icon))
    }
     */

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override fun getCategoriesFromFirestore(): Query {
        TODO("Not yet implemented")
        /*
        if (shouldReturnNetworkError) {
            throw Exception()
        }

        if (categories.size == 0) {
            throw Exception()
        }

        return categories
         */
    }

    override fun getWarrantiesFromFirestore(): Query {
        TODO("Not yet implemented")
        /*
        if (shouldReturnNetworkError) {
            throw Exception()
        }

        if (warranties.size == 0) {
            throw Exception()
        }

        return warranties
        */
    }

    override suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            addWarranty(warrantyInput)
        }
    }

    override suspend fun deleteWarrantyFromFirestore(warrantyId: String) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            if (!warranties.remove(warranties.find { it.id == warrantyId })) {
                throw Exception()
            }
        }
    }

    override suspend fun updateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        if (shouldReturnNetworkError) {
            throw Exception()
        } else {
            if (warranties.remove(warranties.find { it.id == warrantyId })) {
                addWarranty(warrantyInput)
            } else {
                throw Exception()
            }
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