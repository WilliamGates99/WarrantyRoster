package com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.UnlinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.models.AccountProviders
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.UnlinkAccountsRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class UnlinkAccountsRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : UnlinkAccountsRepository {

    override suspend fun unlinkGoogleAccount(): Result<Unit, UnlinkGoogleAccountError> {
        return try {
            firebaseAuth.get().currentUser?.let { currentUser ->
                currentUser.unlink(AccountProviders.GOOGLE.id).await()
                return Result.Success(Unit)
            }

            Result.Error(UnlinkGoogleAccountError.Network.SomethingWentWrong)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Unlink Google account FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(UnlinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Unlink Google account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UnlinkGoogleAccountError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Unlink Google account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UnlinkGoogleAccountError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Unlink Google account FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(UnlinkGoogleAccountError.Network.Firebase403)
                else -> Result.Error(UnlinkGoogleAccountError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Unlink Google account Exception:")
            e.printStackTrace()
            Result.Error(UnlinkGoogleAccountError.Network.SomethingWentWrong)
        }
    }

    override suspend fun unlinkXAccount(): Result<Unit, UnlinkXAccountError> {
        return try {
            firebaseAuth.get().currentUser?.let { currentUser ->
                currentUser.unlink(AccountProviders.X.id).await()
                return Result.Success(Unit)
            }

            Result.Error(UnlinkXAccountError.Network.SomethingWentWrong)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Unlink X account FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(UnlinkXAccountError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Unlink X account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UnlinkXAccountError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Unlink X account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UnlinkXAccountError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Unlink X account FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(UnlinkXAccountError.Network.Firebase403)
                else -> Result.Error(UnlinkXAccountError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Unlink X account Exception:")
            e.printStackTrace()
            Result.Error(UnlinkXAccountError.Network.SomethingWentWrong)
        }
    }

    override suspend fun unlinkGithubAccount(): Result<Unit, UnlinkGithubAccountError> {
        return try {
            firebaseAuth.get().currentUser?.let { currentUser ->
                currentUser.unlink(AccountProviders.GITHUB.id).await()
                return Result.Success(Unit)
            }

            Result.Error(UnlinkGithubAccountError.Network.SomethingWentWrong)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Unlink Github account FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(UnlinkGithubAccountError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Unlink Github account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UnlinkGithubAccountError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Unlink Github account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(UnlinkGithubAccountError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Unlink Github account FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(UnlinkGithubAccountError.Network.Firebase403)
                else -> Result.Error(UnlinkGithubAccountError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Unlink Github account Exception:")
            e.printStackTrace()
            Result.Error(UnlinkGithubAccountError.Network.SomethingWentWrong)
        }
    }
}