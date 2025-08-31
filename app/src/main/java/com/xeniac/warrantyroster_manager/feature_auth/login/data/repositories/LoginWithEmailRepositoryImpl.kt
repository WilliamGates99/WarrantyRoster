package com.xeniac.warrantyroster_manager.feature_auth.login.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.repositories.LoginWithEmailRepository
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class LoginWithEmailRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : LoginWithEmailRepository {

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit, LoginWithEmailError> {
        return try {
            withContext(context = Dispatchers.IO) {
                val result = firebaseAuth.get().signInWithEmailAndPassword(
                    email.trim(),
                    password.trim()
                ).await()

                val isSuccess = result.user != null
                if (isSuccess) {
                    warrantyRosterDataStoreRepository.get().isUserLoggedIn(isLoggedIn = true)
                    return@withContext Result.Success(Unit)
                }

                Result.Error(LoginWithEmailError.Network.SomethingWentWrong)
            }
        } catch (e: SSLHandshakeException) {
            Timber.e("Login with email SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LoginWithEmailError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Login with email CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LoginWithEmailError.Network.CertPathValidatorException)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Login with email Exception:")
            e.printStackTrace()

            // TODO: ADD ERROR HANDLING

            Result.Error(LoginWithEmailError.Network.SomethingWentWrong)
        }
    }
}

/**
 *    const val ERROR_GOOGLE_SIGN_IN_API_NOT_AVAILABLE =
 *         "Auth.GOOGLE_SIGN_IN_API is not available on this device"
 *     const val ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED = "12501"
 *     const val ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE = "7"
 *     const val ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED = "The web operation was canceled by the user"
 *     const val ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION = "An internal error has occurred"
 *     const val ERROR_NETWORK_CONNECTION =
 *         "A network error (such as timeout, interrupted connection or unreachable host) has occurred"
 *     const val ERROR_FIREBASE_403 = "403"
 *     const val ERROR_FIREBASE_DEVICE_BLOCKED =
 *         "We have blocked all requests from this device due to unusual activity"
 *     const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS =
 *         "The email address is already in use by another account"
 *     const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS =
 *         "An account already exists with the same email address but different sign-in credentials"
 *     const val ERROR_FIREBASE_AUTH_ACCOUNT_NOT_FOUND =
 *         "There is no user record corresponding to this identifier"
 *     const val ERROR_FIREBASE_AUTH_CREDENTIALS =
 *         "The password is invalid or the user does not have a password"
 *     const val ERROR_FIREBASE_AUTH_ALREADY_LINKED =
 *         "User has already been linked to the given provider"
 *     const val ERROR_FIREBASE_AUTH_EMAIL_VERIFICATION_EMAIL_NOT_PROVIDED =
 *         "An email address must be provided"
 *     const val ERROR_TIMER_IS_NOT_ZERO = "Timer is not zero"
 *     const val ERROR_EMPTY_CATEGORY_LIST = "Category list is empty"
 *     const val ERROR_EMPTY_WARRANTY_LIST = "Warranty list is empty"
 *     const val ERROR_EMPTY_SEARCH_RESULT_LIST = "Search result list is empty"
 */