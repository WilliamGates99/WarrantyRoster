package com.xeniac.warrantyroster_manager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.di.CategoriesCollection
import com.xeniac.warrantyroster_manager.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.domain.repository.MainRepository
import com.xeniac.warrantyroster_manager.utils.Constants.CATEGORIES_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_LIFETIME
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.WARRANTIES_UUID
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @CategoriesCollection private val categoriesCollectionRef: CollectionReference,
    @WarrantiesCollection private val warrantiesCollectionRef: CollectionReference
) : MainRepository {

    override fun getCategoriesFromFirestore(): Query = categoriesCollectionRef
        .orderBy(CATEGORIES_TITLE, Query.Direction.ASCENDING)

    override fun getWarrantiesFromFirestore(): Query = warrantiesCollectionRef
        .whereEqualTo(WARRANTIES_UUID, firebaseAuth.currentUser?.uid)
        .orderBy(WARRANTIES_TITLE, Query.Direction.ASCENDING)

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

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty {
        warrantiesCollectionRef.document(warrantyId).get().await().apply {
            return Warranty(
                id,
                get(WARRANTIES_TITLE).toString(),
                get(WARRANTIES_BRAND).toString(),
                get(WARRANTIES_MODEL).toString(),
                get(WARRANTIES_SERIAL_NUMBER).toString(),
                get(WARRANTIES_LIFETIME) as Boolean?,
                get(WARRANTIES_STARTING_DATE).toString(),
                get(WARRANTIES_EXPIRY_DATE).toString(),
                get(WARRANTIES_DESCRIPTION).toString(),
                get(WARRANTIES_CATEGORY_ID).toString()
            )
        }
    }
}