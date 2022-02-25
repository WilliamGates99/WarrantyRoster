package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.firebase.FirebaseAuthInstance
import com.xeniac.warrantyroster_manager.firebase.FirestoreInstance
import com.xeniac.warrantyroster_manager.models.WarrantyInput
import com.xeniac.warrantyroster_manager.utils.Constants

class MainRepository {

    fun getCategoriesFromFirestore() = FirestoreInstance.categoriesCollectionRef
        .orderBy(Constants.CATEGORIES_TITLE, Query.Direction.ASCENDING)

    fun getWarrantiesFromFirestore() = FirestoreInstance.warrantiesCollectionRef
        .whereEqualTo(Constants.WARRANTIES_UUID, FirebaseAuthInstance.auth.currentUser?.uid)
        .orderBy(Constants.WARRANTIES_TITLE, Query.Direction.ASCENDING)

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) =
        FirestoreInstance.warrantiesCollectionRef.add(warrantyInput)

    fun deleteWarrantyFromFirestore(warrantyId: String) =
        FirestoreInstance.warrantiesCollectionRef.document(warrantyId).delete()

    fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput) =
        FirestoreInstance.warrantiesCollectionRef.document(warrantyId).set(warrantyInput)

    fun getUpdatedWarrantyFromFirestore(warrantyId: String) =
        FirestoreInstance.warrantiesCollectionRef.document(warrantyId).get()
}