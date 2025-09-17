package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers.toWarranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.MISCELLANEOUS_CATEGORY
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_TITLE
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.utils.Constants.WARRANTIES_UUID
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.Warranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.ObserveWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.errors.SearchWarrantiesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranties.domain.repositories.WarrantiesRepository
import dagger.Lazy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class WarrantiesRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    @WarrantiesCollection private val warrantiesCollectionRef: Lazy<CollectionReference>
) : WarrantiesRepository {

    override fun observeWarranties(
        fetchedCategories: List<WarrantyCategory>?
    ): Flow<Result<List<Warranty>, ObserveWarrantiesError>> = callbackFlow {
        var warrantiesListenerRegistration: ListenerRegistration? = null

        try {
            warrantiesListenerRegistration = warrantiesCollectionRef.get().whereEqualTo(
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
                        try {
                            val warranties = mutableListOf<Warranty>()
                            // var adIndex = 5

                            querySnapshot.documents.forEach { document ->
                                val warrantyDto = document.toObject(
                                    /* valueType = */ WarrantyDto::class.java
                                )?.copy(id = document.id)

                                warrantyDto?.let {
                                    warranties.add(
                                        it.toWarranty(
                                            category = fetchedCategories?.find { category ->
                                                category.id == warrantyDto.categoryId
                                            } ?: MISCELLANEOUS_CATEGORY
                                        )
                                    )
                                }

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
                        } catch (e: Exception) {
                            coroutineContext.ensureActive()
                            Timber.e("Observe warranties query snapshot Exception:")
                            e.printStackTrace()
                            trySend(Result.Error(ObserveWarrantiesError.Network.SomethingWentWrong))
                            close()
                        }
                    }
                }
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Observe warranties FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.FirebaseAuthUnauthorizedUser))
            close()
        } catch (e: FirebaseNetworkException) {
            Timber.e("Observe warranties FirebaseNetworkException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.FirebaseNetworkException))
            close()
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Observe warranties FirebaseNetworkException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.FirebaseTooManyRequestsException))
            close()
        } catch (e: FirebaseException) {
            Timber.e("Observe warranties FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> trySend(Result.Error(ObserveWarrantiesError.Network.Firebase403))
                else -> trySend(Result.Error(ObserveWarrantiesError.Network.SomethingWentWrong))
            }
            close()
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Observe warranties Exception:")
            e.printStackTrace()
            trySend(Result.Error(ObserveWarrantiesError.Network.SomethingWentWrong))
            close()
        }

        awaitClose {
            warrantiesListenerRegistration?.remove()
        }
    }

    override suspend fun searchWarranties(
        warranties: List<Warranty>?,
        query: String,
        delayInMillis: Long
    ): Result<List<Warranty>, SearchWarrantiesError> {
        return try {
            delay(timeMillis = delayInMillis)

            val filteredWarranties = warranties?.filter { warranty ->
                warranty.title.contains(
                    other = query,
                    ignoreCase = true
                )
            } ?: emptyList()

            Result.Success(filteredWarranties)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Search warranties Exception:")
            e.printStackTrace()
            Result.Error(SearchWarrantiesError.SomethingWentWrong)
        }
    }
}