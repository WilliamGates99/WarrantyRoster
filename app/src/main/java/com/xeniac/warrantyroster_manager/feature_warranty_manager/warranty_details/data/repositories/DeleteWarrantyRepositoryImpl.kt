package com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.di.WarrantiesCollection
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.presentation.common.utils.UiText
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.errors.DeleteWarrantyError
import com.xeniac.warrantyroster_manager.feature_warranty_manager.warranty_details.domain.repositories.DeleteWarrantyRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class DeleteWarrantyRepositoryImpl @Inject constructor(
    @WarrantiesCollection private val warrantiesCollectionRef: Lazy<CollectionReference>
) : DeleteWarrantyRepository {

    override suspend fun deleteWarranty(
        warrantyId: String
    ): Result<Unit, DeleteWarrantyError> {
        return try {
            warrantiesCollectionRef.get().document(
                /* documentPath = */ warrantyId
            ).delete().await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Delete warranty FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(DeleteWarrantyError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseFirestoreException) {
            Timber.e("Delete warranty FirebaseFirestoreException:")
            e.printStackTrace()
            Result.Error(
                DeleteWarrantyError.Network.FirebaseFirestoreException(
                    UiText.DynamicString(e.localizedMessage ?: e.message.orEmpty())
                )
            )
        } catch (e: FirebaseNetworkException) {
            Timber.e("Delete warranty FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(DeleteWarrantyError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Delete warranty FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(DeleteWarrantyError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Delete warranty FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(DeleteWarrantyError.Network.Firebase403)
                else -> Result.Error(DeleteWarrantyError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Delete warranty Exception:")
            e.printStackTrace()
            Result.Error(DeleteWarrantyError.Network.SomethingWentWrong)
        }
    }
}