package com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.CollectionReference
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.di.CategoriesCollection
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.SettingsDataStoreRepository
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.mappers.toWarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.data.remote.WarrantyCategoryDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.errors.ObserveCategoriesError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.models.WarrantyCategory
import com.xeniac.warrantyroster_manager.feature_warranty_manager.common.domain.repositories.CategoriesRepository
import dagger.Lazy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    @CategoriesCollection private val categoriesCollectionRef: Lazy<CollectionReference>,
    private val settingsDataStoreRepository: Lazy<SettingsDataStoreRepository>
) : CategoriesRepository {

    override fun observeCategories(
    ): Flow<Result<List<WarrantyCategory>, ObserveCategoriesError>> = callbackFlow {
        try {
            categoriesCollectionRef.get().addSnapshotListener { querySnapshot, exception ->
                launch {
                    exception?.let { e ->
                        coroutineContext.ensureActive()
                        Timber.e("Observe categories FirebaseFirestoreException:")
                        e.printStackTrace()
                        send(
                            Result.Error(
                                ObserveCategoriesError.Network.FirebaseFirestoreException(
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
                            val categories = mutableListOf<WarrantyCategory>()

                            querySnapshot.documents.forEach { document ->
                                val categoryDto = document.toObject(
                                    /* valueType = */ WarrantyCategoryDto::class.java
                                )?.copy(id = document.id)

                                categoryDto?.let {
                                    categories.add(it.toWarrantyCategory())
                                }
                            }

                            categories.sortBy {
                                val currentAppLanguageTag = settingsDataStoreRepository.get()
                                    .getCurrentAppLocale()
                                    .languageTag
                                it.title[currentAppLanguageTag]
                            }

                            send(Result.Success(categories))
                        } catch (e: Exception) {
                            coroutineContext.ensureActive()
                            Timber.e("Observe categories query snapshot Exception:")
                            e.printStackTrace()
                            trySend(Result.Error(ObserveCategoriesError.Network.SomethingWentWrong))
                        }
                    }
                }
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Observe categories FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveCategoriesError.Network.FirebaseAuthUnauthorizedUser))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Observe categories FirebaseNetworkException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveCategoriesError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Observe categories FirebaseNetworkException:")
            e.printStackTrace()
            trySend(Result.Error(ObserveCategoriesError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Observe categories FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> trySend(Result.Error(ObserveCategoriesError.Network.Firebase403))
                else -> trySend(Result.Error(ObserveCategoriesError.Network.SomethingWentWrong))
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Observe categories Exception:")
            e.printStackTrace()
            trySend(Result.Error(ObserveCategoriesError.Network.SomethingWentWrong))
        }

        awaitClose { }
    }
}