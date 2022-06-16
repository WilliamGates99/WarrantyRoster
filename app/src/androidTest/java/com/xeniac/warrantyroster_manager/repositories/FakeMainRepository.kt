package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.TestUser
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput

class FakeMainRepository : MainRepository {

    private val warranties = mutableListOf<WarrantyInput>()

    private var shouldReturnNetworkError = false

    // TODO EDIT
    fun addWarranty(warrantyInput: WarrantyInput) {
        warranties.add(warrantyInput)
    }

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override fun getCategoriesFromFirestore(): Query {
        TODO("Not yet implemented")
    }

    override fun getWarrantiesFromFirestore(): Query {
        TODO("Not yet implemented")
    }

    override suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWarrantyFromFirestore(warrantyId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Any {
        TODO("Not yet implemented")
    }
}