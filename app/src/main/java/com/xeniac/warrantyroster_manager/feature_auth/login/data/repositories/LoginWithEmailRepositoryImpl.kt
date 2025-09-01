package com.xeniac.warrantyroster_manager.feature_auth.login.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.models.LoginWithEmailResult
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
    ): LoginWithEmailResult {
        return try {
            withContext(context = Dispatchers.IO) {
                val result = firebaseAuth.get().signInWithEmailAndPassword(
                    email.trim(),
                    password.trim()
                ).await()

                val isSuccess = result.user != null
                if (isSuccess) {
                    warrantyRosterDataStoreRepository.get().isUserLoggedIn(isLoggedIn = true)
                    return@withContext LoginWithEmailResult(result = Result.Success(Unit))
                }

                LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.SomethingWentWrong))

            }
        } catch (e: IllegalArgumentException) {
            Timber.e("Login with email IllegalArgumentException:")
            e.printStackTrace()
            LoginWithEmailResult(
                emailError = LoginWithEmailError.BlankEmail,
                passwordError = LoginWithEmailError.BlankPassword
            )
        } catch (e: SSLHandshakeException) {
            Timber.e("Login with email SSLHandshakeException:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.SSLHandshakeException))
        } catch (e: CertPathValidatorException) {
            Timber.e("Login with email CertPathValidatorException:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.CertPathValidatorException))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Login with email FirebaseNetworkException:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Login with email FirebaseNetworkException:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Login with email FirebaseException:")
            e.printStackTrace()

            when {
                isFirebase403Error(e.message) -> LoginWithEmailResult(
                    result = Result.Error(LoginWithEmailError.Network.Firebase403)
                )
                else -> LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.SomethingWentWrong))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Login with email FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.FirebaseAuthInvalidUserException))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Login with email FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.FirebaseAuthInvalidCredentialsException))
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Login with email Exception:")
            e.printStackTrace()
            LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.SomethingWentWrong))
        }
    }
}

/**
 * const val ERROR_GOOGLE_SIGN_IN_API_NOT_AVAILABLE = "Auth.GOOGLE_SIGN_IN_API is not available on this device"
 * const val ERROR_GOOGLE_SIGN_IN_CLIENT_CANCELED = "12501"
 * const val ERROR_GOOGLE_SIGN_IN_CLIENT_OFFLINE = "7"
 *
 * const val ERROR_TWITTER_O_AUTH_PROVIDER_CANCELED = "The web operation was canceled by the user"
 * const val ERROR_TWITTER_O_AUTH_PROVIDER_NETWORK_CONNECTION = "An internal error has occurred"
 *
 * const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS = "The email address is already in use by another account"
 * const val ERROR_FIREBASE_AUTH_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIALS = "An account already exists with the same email address but different sign-in credentials"
 * const val ERROR_FIREBASE_AUTH_ALREADY_LINKED = "User has already been linked to the given provider"
 * const val ERROR_FIREBASE_AUTH_EMAIL_VERIFICATION_EMAIL_NOT_PROVIDED = "An email address must be provided"
 *
 *   catch (e: FirebaseAuthUserCollisionException) { for both ACCOUNT_EXISTS and DIFFERENT_CREDENTIALS
 *             Timber.e("Login with email FirebaseAuthInvalidCredentialsException:")
 *             e.printStackTrace()
 *             LoginWithEmailResult(result = Result.Error(LoginWithEmailError.Network.FirebaseAuthInvalidCredentialsException))
 *         }
 *
 *
 *
 * const val ERROR_TIMER_IS_NOT_ZERO = "Timer is not zero"
 * const val ERROR_EMPTY_CATEGORY_LIST = "Category list is empty"
 * const val ERROR_EMPTY_WARRANTY_LIST = "Warranty list is empty"
 * const val ERROR_EMPTY_SEARCH_RESULT_LIST = "Search result list is empty"
 */