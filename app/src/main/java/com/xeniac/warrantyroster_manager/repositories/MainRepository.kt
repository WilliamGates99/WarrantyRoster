package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.di.CategoriesCollection
import com.xeniac.warrantyroster_manager.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.utils.Constants
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @CategoriesCollection private val categoriesCollectionRef: CollectionReference,
    @WarrantiesCollection private val warrantiesCollectionRef: CollectionReference
) {

    fun getCategoriesFromFirestore() = categoriesCollectionRef
        .orderBy(Constants.CATEGORIES_TITLE, Query.Direction.ASCENDING)

    fun getWarrantiesFromFirestore() = warrantiesCollectionRef
        .whereEqualTo(Constants.WARRANTIES_UUID, firebaseAuth.currentUser?.uid)
        .orderBy(Constants.WARRANTIES_TITLE, Query.Direction.ASCENDING)

    fun addWarrantyToFirestore(warrantyInput: WarrantyInput) =
        warrantiesCollectionRef.add(warrantyInput)

    fun deleteWarrantyFromFirestore(warrantyId: String) =
        warrantiesCollectionRef.document(warrantyId).delete()

    fun updateWarrantyInFirestore(warrantyId: String, warrantyInput: WarrantyInput) =
        warrantiesCollectionRef.document(warrantyId).set(warrantyInput)

    fun getUpdatedWarrantyFromFirestore(warrantyId: String) =
        warrantiesCollectionRef.document(warrantyId).get()
}