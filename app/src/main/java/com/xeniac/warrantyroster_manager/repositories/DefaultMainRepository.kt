package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.di.CategoriesCollection
import com.xeniac.warrantyroster_manager.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @CategoriesCollection private val categoriesCollectionRef: CollectionReference,
    @WarrantiesCollection private val warrantiesCollectionRef: CollectionReference
) : MainRepository {

    override fun getCategoriesFromFirestore(): Query = categoriesCollectionRef
        .orderBy(Constants.CATEGORIES_TITLE, Query.Direction.ASCENDING)

    override fun getWarrantiesFromFirestore(): Query = warrantiesCollectionRef
        .whereEqualTo(Constants.WARRANTIES_UUID, firebaseAuth.currentUser?.uid)
        .orderBy(Constants.WARRANTIES_TITLE, Query.Direction.ASCENDING)

    override suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        warrantiesCollectionRef.add(warrantyInput).await()
    }

    override suspend fun deleteWarrantyFromFirestore(warrantyId: String) {
        warrantiesCollectionRef.document(warrantyId).delete().await()
    }

    override suspend fun updateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        warrantiesCollectionRef.document(warrantyId).set(warrantyInput).await()
    }

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): DocumentSnapshot =
        warrantiesCollectionRef.document(warrantyId).get().await()
}