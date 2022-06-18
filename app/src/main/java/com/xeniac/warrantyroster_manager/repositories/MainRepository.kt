package com.xeniac.warrantyroster_manager.repositories

import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput

interface MainRepository {

    fun getCategoriesFromFirestore(): MutableList<Category>

    fun getWarrantiesFromFirestore(): MutableList<Warranty>

    suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput)

    suspend fun deleteWarrantyFromFirestore(warrantyId: String)

    suspend fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput)

    suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty
}