package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers.toWarranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_BRAND
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_CATEGORY_ID
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_DESCRIPTION
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_EXPIRY_DATE
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_IS_LIFETIME
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_MODEL
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_SERIAL_NUMBER
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_STARTING_DATE
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_UUID
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
import dagger.Lazy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class WarrantiesRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val firestore: Lazy<FirebaseFirestore>,
    @WarrantiesCollection private val warrantiesCollectionRef: Lazy<CollectionReference>
) : WarrantiesRepository {

    override fun observeWarranties(
    ): Flow<Result<List<Warranty>, ObserveWarrantiesError>> = callbackFlow {
        try {
            warrantiesCollectionRef.get().whereEqualTo(
                /* field = */ WARRANTIES_UUID,
                /* value = */ firebaseAuth.get().currentUser?.uid
            ).orderBy(
                /* field = */ WARRANTIES_TITLE,
                /* direction = */ Query.Direction.ASCENDING
            ).addSnapshotListener { querySnapshot, exception ->
                launch {
                    exception?.let { e ->
                        coroutineContext.ensureActive()
                        Timber.e("Observe warranties FirebaseFirestoreException:")
                        e.printStackTrace()
                        send(
                            Result.Error(
                                ObserveWarrantiesError.Network.FirebaseFirestoreException(
                                    message = UiText.DynamicString(
                                        e.localizedMessage ?: e.message.orEmpty()
                                    )
                                )
                            )
                        )
                        close()
                    }

                    querySnapshot?.let {
                        // TODO: MERGE WITH CATEGORIES
                        if (querySnapshot.documents.isEmpty()) {
                            send(Result.Success(emptyList()))
                        }

                        val warranties = mutableListOf<Warranty>()
                        // var adIndex = 5

                        querySnapshot.documents.forEach { document ->
                            val warrantyDto = WarrantyDto(
                                id = document.id,
                                title = document.get(WARRANTIES_TITLE) as String?,
                                brand = document.get(WARRANTIES_BRAND) as String?,
                                model = document.get(WARRANTIES_MODEL) as String?,
                                serialNumber = document.get(WARRANTIES_SERIAL_NUMBER) as String?,
                                description = document.get(WARRANTIES_DESCRIPTION) as String?,
                                categoryId = document.get(WARRANTIES_CATEGORY_ID) as String?,
                                isLifetime = document.get(WARRANTIES_IS_LIFETIME) as Boolean?,
                                startingDate = document.get(WARRANTIES_STARTING_DATE) as String?,
                                expiryDate = document.get(WARRANTIES_EXPIRY_DATE) as String?
                            )

                            warranties.add(warrantyDto.toWarranty())

                            /* TODO: UNCOMMENT AFTER IMPLEMENTING ADS
                            if (warranties.size == adIndex) {
                                adIndex += 6

                                val nativeAd = WarrantyDto(
                                    id = "ad_$adIndex"
                                ).toWarranty().copy(itemType = ListItemType.AD)

                                warranties.add(nativeAd)
                            }
                             */
                        }

                        send(Result.Success(warranties))
                    }
                }
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Observe warranties FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.FirebaseAuthUnauthorizedUser))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Observe warranties FirebaseNetworkException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Observe warranties FirebaseNetworkException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Observe warranties FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> trySend(Result.Error(ObserveWarrantiesError.Network.Firebase403))
                else -> trySend(Result.Error(ObserveWarrantiesError.Network.SomethingWentWrong))
            }
        } catch (e: Exception) {
            kotlin.coroutines.coroutineContext.ensureActive()
            Timber.e("Observe warranties Exception:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.SomethingWentWrong))
        }

        awaitClose { }
    }
}