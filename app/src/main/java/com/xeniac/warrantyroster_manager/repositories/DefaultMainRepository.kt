package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.data.remote.models.Category
import com.xeniac.warrantyroster_manager.data.remote.models.ListItemType
import com.xeniac.warrantyroster_manager.data.remote.models.Warranty
import com.xeniac.warrantyroster_manager.data.remote.models.WarrantyInput
import com.xeniac.warrantyroster_manager.di.CategoriesCollection
import com.xeniac.warrantyroster_manager.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.utils.Constants.CATEGORIES_ICON
import com.xeniac.warrantyroster_manager.utils.Constants.CATEGORIES_TITLE
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_CATEGORY_LIST
import com.xeniac.warrantyroster_manager.utils.Constants.ERROR_EMPTY_WARRANTY_LIST
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

class DefaultMainRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @CategoriesCollection private val categoriesCollectionRef: CollectionReference,
    @WarrantiesCollection private val warrantiesCollectionRef: CollectionReference
) : MainRepository {

    override fun getCategoriesFromFirestore(): MutableList<Category> {
        val categoriesList = mutableListOf<Category>()
        categoriesCollectionRef.orderBy(CATEGORIES_TITLE, Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    throw Exception(it.message)
                }

                value?.let {
                    if (it.documents.size == 0) {
                        throw Exception(ERROR_EMPTY_CATEGORY_LIST)
                    } else {
                        for (document in it.documents) {
                            @Suppress("UNCHECKED_CAST")
                            val category = Category(
                                document.id,
                                document.get(CATEGORIES_TITLE) as Map<String, String>,
                                document.get(CATEGORIES_ICON).toString()
                            )
                            categoriesList.add(category)
                        }
                    }
                }
            }
        return categoriesList
    }

    override fun getWarrantiesFromFirestore(): MutableList<Warranty> {
        val warrantiesList = mutableListOf<Warranty>()
        warrantiesCollectionRef.whereEqualTo(WARRANTIES_UUID, firebaseAuth.currentUser?.uid)
            .orderBy(WARRANTIES_TITLE, Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    throw Exception(it.message)
                }

                value?.let {
                    if (it.documents.size == 0) {
                        throw Exception(ERROR_EMPTY_WARRANTY_LIST)
                    } else {
                        var adIndex = 5

                        for (document in it.documents) {
                            val warranty = Warranty(
                                document.id,
                                document.get(WARRANTIES_TITLE).toString(),
                                document.get(WARRANTIES_BRAND).toString(),
                                document.get(WARRANTIES_MODEL).toString(),
                                document.get(WARRANTIES_SERIAL_NUMBER).toString(),
                                document.get(WARRANTIES_LIFETIME) as Boolean?,
                                document.get(WARRANTIES_STARTING_DATE).toString(),
                                document.get(WARRANTIES_EXPIRY_DATE).toString(),
                                document.get(WARRANTIES_DESCRIPTION).toString(),
                                document.get(WARRANTIES_CATEGORY_ID).toString()
                            )
                            warrantiesList.add(warranty)

                            if (warrantiesList.size == adIndex) {
                                adIndex += 6
                                val nativeAd = Warranty(
                                    id = adIndex.toString(),
                                    isLifetime = null,
                                    categoryId = null,
                                    itemType = ListItemType.AD
                                )
                                warrantiesList.add(nativeAd)
                            }
                        }
                    }
                }
            }
        return warrantiesList
    }

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