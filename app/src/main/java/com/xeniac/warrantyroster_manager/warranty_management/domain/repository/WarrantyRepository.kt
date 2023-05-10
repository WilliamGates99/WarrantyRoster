package com.xeniac.warrantyroster_manager.warranty_management.domain.repository

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput

interface WarrantyRepository {

    fun getWarrantiesFromFirestore(): Query

    suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput)

    suspend fun deleteWarrantyFromFirestore(warrantyId: String)

    suspend fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput)

    suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty
}