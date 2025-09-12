package com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWebException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isAnotherAuthOperationInProgress
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebaseCancellationException
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkXAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkXAccountRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class LinkXAccountRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : LinkXAccountRepository {

    override suspend fun checkPendingLinkXAccount(): Result<Task<AuthResult>?, LinkXAccountError> {
        return try {
            firebaseAuth.get().pendingAuthResult?.let { pendingLoginWithXTask ->
                return Result.Success(pendingLoginWithXTask)
            }

            Result.Success(null)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Check pending link X account Exception:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.SomethingWentWrong)
        }
    }

    override suspend fun linkXAccount(
        linkXAccountTask: Task<AuthResult>
    ): Result<Unit, LinkXAccountError> {
        return try {
            val linkXAccountResult = linkXAccountTask.await()

            linkXAccountResult.credential?.let { authCredential ->
                firebaseAuth.get().currentUser?.let { currentUser ->
                    currentUser.linkWithCredential(authCredential).await()
                    return Result.Success(Unit)
                }
            }

            Result.Error(LinkXAccountError.Network.SomethingWentWrong)
        } catch (e: SSLHandshakeException) {
            Timber.e("Link X account SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Link X account CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.CertPathValidatorException)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Link X account FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Link X account FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.FirebaseAuthInvalidCredentialsException)
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Link X account FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.FirebaseAuthUserCollisionException)
        } catch (e: FirebaseAuthWebException) {
            Timber.e("Link X account FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                isFirebaseCancellationException(e.message) -> Result.Error(LinkXAccountError.CancellationException)
                isAnotherAuthOperationInProgress(e.message) -> Result.Error(LinkXAccountError.AnotherOperationIsInProgress)
                else -> Result.Error(LinkXAccountError.Network.SomethingWentWrong)
            }
        } catch (e: FirebaseNetworkException) {
            Timber.e("Link X account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Link X account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Link X account FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                e.message?.contains(
                    other = "User has already been linked to the given provider.",
                    ignoreCase = true
                ) == true -> {
                    // Firebase Auth gives this exception, even though the account is linked successfully
                    Result.Success(Unit)
                }
                isFirebase403Error(e.message) -> Result.Error(LinkXAccountError.Network.Firebase403)
                e.message?.contains(
                    other = "An internal error has occurred",
                    ignoreCase = true
                ) == true -> Result.Error(LinkXAccountError.Network.FirebaseNetworkException)
                else -> Result.Error(LinkXAccountError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Link X account Exception:")
            e.printStackTrace()
            Result.Error(LinkXAccountError.Network.SomethingWentWrong)
        }
    }
}