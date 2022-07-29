package com.xeniac.warrantyroster_manager.domain.repository

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput

interface MainRepository {

    fun getCategoriesFromFirestore(): Query

    fun getWarrantiesFromFirestore(): Query

    suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput)

    suspend fun deleteWarrantyFromFirestore(warrantyId: String)

    suspend fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput)

    suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty
}