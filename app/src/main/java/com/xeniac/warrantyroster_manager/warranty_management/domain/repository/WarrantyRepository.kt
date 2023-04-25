package com.xeniac.warrantyroster_manager.warranty_management.domain.repository

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyInput

interface WarrantyRepository {

    fun getCategoriesFromFirestore(): Query

    fun getWarrantiesFromFirestore(): Query

    suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput)

    suspend fun deleteWarrantyFromFirestore(warrantyId: String)

    suspend fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput)

    suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty
}