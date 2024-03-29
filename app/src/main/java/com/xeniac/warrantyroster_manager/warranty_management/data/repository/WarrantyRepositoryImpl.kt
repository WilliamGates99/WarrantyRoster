package com.xeniac.warrantyroster_manager.warranty_management.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_LIFETIME
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.util.Constants.WARRANTIES_UUID
import com.xeniac.warrantyroster_manager.warranty_management.data.mapper.toWarranty
import com.xeniac.warrantyroster_manager.warranty_management.data.mapper.toWarrantyInputDto
import com.xeniac.warrantyroster_manager.warranty_management.data.remote.dto.WarrantyDto
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.Warranty
import com.xeniac.warrantyroster_manager.warranty_management.domain.model.WarrantyInput
import com.xeniac.warrantyroster_manager.warranty_management.domain.repository.WarrantyRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WarrantyRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @WarrantiesCollection private val warrantiesCollectionRef: CollectionReference
) : WarrantyRepository {

    override fun getWarrantiesFromFirestore(): Query = warrantiesCollectionRef
        .whereEqualTo(WARRANTIES_UUID, firebaseAuth.currentUser?.uid)
        .orderBy(WARRANTIES_TITLE, Query.Direction.ASCENDING)

    override suspend fun addWarrantyToFirestore(warrantyInput: WarrantyInput) {
        warrantiesCollectionRef.add(warrantyInput.toWarrantyInputDto()).await()
    }

    override suspend fun deleteWarrantyFromFirestore(warrantyId: String) {
        warrantiesCollectionRef.document(warrantyId).delete().await()
    }

    override suspend fun updateWarrantyInFirestore(
        warrantyId: String, warrantyInput: WarrantyInput
    ) {
        warrantiesCollectionRef.document(warrantyId).set(warrantyInput.toWarrantyInputDto()).await()
    }

    override suspend fun getUpdatedWarrantyFromFirestore(warrantyId: String): Warranty {
        warrantiesCollectionRef.document(warrantyId).get().await().apply {
            return WarrantyDto(
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
            ).toWarranty()
        }
    }
}