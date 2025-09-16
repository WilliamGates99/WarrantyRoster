package com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.data.mappers.toUpsertingWarrantyDto
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.errors.UpsertWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.models.UpsertingWarranty
import com.xeniac.warrantyroster_manager.feature_warranty_manager.upsert_warranty.domain.repositories.AddWarrantyRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AddWarrantyRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    @WarrantiesCollection private val warrantiesCollectionRef: Lazy<CollectionReference>
) : AddWarrantyRepository {

    override suspend fun addWarranty(
        addingWarranty: UpsertingWarranty
    ): Result<Unit, UpsertWarrantyError> {
        return try {
            firebaseAuth.get().currentUser?.let { currentUser ->
                val addingWarrantyDto = addingWarranty.toUpsertingWarrantyDto(
                    uuid = currentUser.uid
                )

                warrantiesCollectionRef.get().add(
                    /* data = */ addingWarrantyDto
                ).await()

                Result.Success(Unit)
            } ?: Result.Error(UpsertWarrantyError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Add warranty FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(UpsertWarrantyError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseFirestoreException) {
            Timber.e("Add warranty FirebaseFirestoreException:")
            e.printStackTrace()
            Result.Error(
                UpsertWarrantyError.Network.FirebaseFirestoreException(
                    UiText.DynamicString(e.localizedMessage ?: e.message.orEmpty())
                )
            )
        } catch (e: FirebaseNetworkException) {
            Timber.e("Add warranty FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UpsertWarrantyError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Add warranty FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UpsertWarrantyError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Add warranty FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(UpsertWarrantyError.Network.Firebase403)
                else -> Result.Error(UpsertWarrantyError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Add warranty Exception:")
            e.printStackTrace()
            Result.Error(UpsertWarrantyError.Network.SomethingWentWrong)
        }
    }
}