package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.db.WarrantyRosterDatabase
import com.xeniac.warrantyroster_manager.firebase.FirebaseAuthInstance
import com.xeniac.warrantyroster_manager.firebase.FirestoreInstance
import com.xeniac.warrantyroster_manager.models.Category
import com.xeniac.warrantyroster_manager.models.WarrantyInput
import com.xeniac.warrantyroster_manager.utils.Constants

class WarrantyRepository(private val db: WarrantyRosterDatabase) {

    suspend fun insertAllCategories(categoriesList: List<Category>) =
        db.getCategoryDao().insertAllCategories(categoriesList)

    suspend fun deleteAllCategories() = db.getCategoryDao().deleteAllCategories()

    fun countItems() = db.getCategoryDao().countItems()

    fun getAllCategories() = db.getCategoryDao().getAllCategories()

    fun getCategoryById(categoryId: String) = db.getCategoryDao().getCategoryById(categoryId)

    fun getCategoriesFromFirestore() = FirestoreInstance.categoriesCollectionRef
        .orderBy(Constants.CATEGORIES_TITLE, Query.Direction.ASCENDING)
        .get()

    fun getWarrantiesFromFirestore() = FirestoreInstance.warrantiesCollectionRef
        .whereEqualTo(Constants.WARRANTIES_UUID, FirebaseAuthInstance.auth.currentUser?.uid)
        .orderBy(Constants.WARRANTIES_TITLE, Query.Direction.ASCENDING)

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) =
        FirestoreInstance.warrantiesCollectionRef.add(warrantyInput)

    fun deleteWarrantyFromFirestore(warrantyId: String) =
        FirestoreInstance.warrantiesCollectionRef.document(warrantyId).delete()
}