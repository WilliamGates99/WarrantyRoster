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
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isAnotherAuthOperationInProgress
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebaseCancellationException
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithXError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithXRepository
import dagger.Lazy
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class LoginWithXRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : LoginWithXRepository {

    override suspend fun checkPendingLoginWithX(): Result<Task<AuthResult>?, LoginWithXError> {
        return try {
            firebaseAuth.get().pendingAuthResult?.let { pendingLoginWithXTask ->
                return Result.Success(pendingLoginWithXTask)
            }

            Result.Success(null)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Check pending login with X Exception:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.SomethingWentWrong)
        }
    }

    override suspend fun loginWithX(
        loginWithXTask: Task<AuthResult>
    ): Result<Unit, LoginWithXError> {
        return try {
            val loginWithXResult = loginWithXTask.await()

            loginWithXResult.credential?.let { authCredential ->
                val authResult = firebaseAuth.get().signInWithCredential(authCredential).await()

                val isSuccess = authResult.user != null
                if (isSuccess) {
                    warrantyRosterDataStoreRepository.get().isUserLoggedIn(
                        isLoggedIn = true
                    )
                    return Result.Success(Unit)
                }
            }

            Result.Error(LoginWithXError.Network.SomethingWentWrong)
        } catch (e: SSLHandshakeException) {
            Timber.e("Login with X SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Login with X CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.CertPathValidatorException)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Login with X FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.FirebaseAuthInvalidUserException)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Login with X FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.FirebaseAuthInvalidCredentialsException)
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Login with X FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.FirebaseAuthUserCollisionException)
        } catch (e: FirebaseAuthWebException) {
            Timber.e("Login with X FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                isFirebaseCancellationException(e.message) -> Result.Error(LoginWithXError.CancellationException)
                isAnotherAuthOperationInProgress(e.message) -> Result.Error(LoginWithXError.AnotherOperationIsInProgress)
                else -> Result.Error(LoginWithXError.Network.SomethingWentWrong)
            }
        } catch (e: FirebaseNetworkException) {
            Timber.e("Login with X FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Login with X FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Login with X FirebaseAuthWebException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(LoginWithXError.Network.Firebase403)
                e.message?.contains(
                    other = "An internal error has occurred",
                    ignoreCase = true
                ) == true -> Result.Error(LoginWithXError.Network.FirebaseNetworkException)
                else -> Result.Error(LoginWithXError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Login with X Exception:")
            e.printStackTrace()
            Result.Error(LoginWithXError.Network.SomethingWentWrong)
        }
    }
}