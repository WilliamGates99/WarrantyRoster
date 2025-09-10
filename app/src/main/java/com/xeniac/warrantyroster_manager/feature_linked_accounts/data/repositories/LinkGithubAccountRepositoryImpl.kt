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
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGithubAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGithubAccountRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class LinkGithubAccountRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : LinkGithubAccountRepository {

    override suspend fun checkPendingLinkGithubAccount(): Result<Task<AuthResult>?, LinkGithubAccountError> {
        return try {
            firebaseAuth.get().pendingAuthResult?.let { pendingLoginWithGithubTask ->
                return Result.Success(pendingLoginWithGithubTask)
            }

            Result.Success(null)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Check pending link Github account Exception:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.SomethingWentWrong)
        }
    }

    override suspend fun linkGithubAccount(
        linkGithubAccountTask: Task<AuthResult>
    ): Result<Unit, LinkGithubAccountError> {
        return try {
            val linkGithubAccountResult = linkGithubAccountTask.await()

            linkGithubAccountResult.credential?.let { authCredential ->
                firebaseAuth.get().currentUser?.let { currentUser ->
                    currentUser.linkWithCredential(authCredential).await()
                    return Result.Success(Unit)
                }
            }

            Result.Error(LinkGithubAccountError.Network.SomethingWentWrong)
        } catch (e: SSLHandshakeException) {
            Timber.e("Link Github account SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Link Github account CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.CertPathValidatorException)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Link Github account FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Link Github account FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.FirebaseAuthInvalidCredentialsException)
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Link Github account FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.FirebaseAuthUserCollisionException)
        } catch (e: FirebaseAuthWebException) {
            Timber.e("Link Github account FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                e.message?.contains(
                    other = "The web operation was canceled",
                    ignoreCase = true
                ) == true -> Result.Error(LinkGithubAccountError.CancellationException)
                else -> Result.Error(LinkGithubAccountError.Network.SomethingWentWrong)
            }
        } catch (e: FirebaseNetworkException) {
            Timber.e("Link Github account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Link Github account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Link Github account FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(LinkGithubAccountError.Network.Firebase403)
                e.message?.contains(
                    other = "An internal error has occurred",
                    ignoreCase = true
                ) == true -> Result.Error(LinkGithubAccountError.Network.FirebaseNetworkException)
                else -> Result.Error(LinkGithubAccountError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Link Github account Exception:")
            e.printStackTrace()
            Result.Error(LinkGithubAccountError.Network.SomethingWentWrong)
        }
    }
}