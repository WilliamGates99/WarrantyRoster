package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

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
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGithubError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGithubRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class LoginWithGithubRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : LoginWithGithubRepository {

    override suspend fun checkPendingLoginWithGithub(): Result<Task<AuthResult>?, LoginWithGithubError> {
        return try {
            firebaseAuth.get().pendingAuthResult?.let { pendingLoginWithGithubTask ->
                return Result.Success(pendingLoginWithGithubTask)
            }

            Result.Success(null)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Check pending login with Github Exception:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.SomethingWentWrong)
        }
    }

    override suspend fun loginWithGithub(
        loginWithGithubTask: Task<AuthResult>
    ): Result<Unit, LoginWithGithubError> {
        return try {
            val loginWithGithubResult = loginWithGithubTask.await()

            loginWithGithubResult.credential?.let { authCredential ->
                val authResult = firebaseAuth.get().signInWithCredential(authCredential).await()

                val isSuccess = authResult.user != null
                if (isSuccess) {
                    warrantyRosterDataStoreRepository.get().isUserLoggedIn(
                        isLoggedIn = true
                    )
                    return Result.Success(Unit)
                }
            }

            Result.Error(LoginWithGithubError.Network.SomethingWentWrong)
        } catch (e: SSLHandshakeException) {
            Timber.e("Login with Github SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Login with Github CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.CertPathValidatorException)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Login with Github FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.FirebaseAuthInvalidUserException)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Login with Github FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.FirebaseAuthInvalidCredentialsException)
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Login with Github FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.FirebaseAuthUserCollisionException)
        } catch (e: FirebaseAuthWebException) {
            Timber.e("Login with Github FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                e.message?.contains(
                    other = "The web operation was canceled",
                    ignoreCase = true
                ) == true -> Result.Error(LoginWithGithubError.CancellationException)
                else -> Result.Error(LoginWithGithubError.Network.SomethingWentWrong)
            }
        } catch (e: FirebaseNetworkException) {
            Timber.e("Login with Github FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Login with Github FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Login with Github FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(LoginWithGithubError.Network.Firebase403)
                e.message?.contains(
                    other = "An internal error has occurred",
                    ignoreCase = true
                ) == true -> Result.Error(LoginWithGithubError.Network.FirebaseNetworkException)
                else -> Result.Error(LoginWithGithubError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Login with Github Exception:")
            e.printStackTrace()
            Result.Error(LoginWithGithubError.Network.SomethingWentWrong)
        }
    }
}