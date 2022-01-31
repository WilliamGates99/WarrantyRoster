package com.xeniac.warrantyroster_manager.repositories

import com.xeniac.warrantyroster_manager.db.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.firebase.FirestoreInstance
import com.xeniac.warrantyroster_manager.models.Category
import com.xeniac.warrantyroster_manager.models.WarrantyInput

class WarrantyRepository(private val db: WarrantyRosterDatabase) {

    suspend fun insertAllCategories(categoriesList: List<Category>) =
        db.getCategoryDao().insertAllCategories(categoriesList)

    suspend fun deleteAllCategories() = db.getCategoryDao().deleteAllCategories()

    fun countItems() = db.getCategoryDao().countItems()

    fun getAllCategories() = db.getCategoryDao().getAllCategories()

    fun getCategoryById(categoryId: String) = db.getCategoryDao().getCategoryById(categoryId)

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) =
        FirestoreInstance.warrantiesCollectionRef.add(warrantyInput)
}