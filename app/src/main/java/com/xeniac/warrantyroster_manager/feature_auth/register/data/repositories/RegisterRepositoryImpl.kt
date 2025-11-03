package com.xeniac.warrantyroster_manager.feature_auth.register.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.models.RegisterWithEmailResult
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.repositories.RegisterRepository
import dagger.Lazy
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : RegisterRepository {

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): RegisterWithEmailResult {
        return try {
            val authResult = with(firebaseAuth.get()) {
                useAppLanguage()
                createUserWithEmailAndPassword(
                    email.lowercase().trim(),
                    password.trim()
                ).await()
            }

            authResult.user?.let { registeredUser ->
                warrantyRosterDataStoreRepository.get().isUserLoggedIn(isLoggedIn = true)
                registeredUser.sendEmailVerification().await()
                return RegisterWithEmailResult(result = Result.Success(Unit))
            }

            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.SomethingWentWrong))
        } catch (e: IllegalArgumentException) {
            Timber.e("Register with email IllegalArgumentException:")
            e.printStackTrace()
            RegisterWithEmailResult(
                emailError = RegisterWithEmailError.BlankEmail,
                passwordError = RegisterWithEmailError.BlankPassword,
                confirmPasswordError = RegisterWithEmailError.BlankConfirmPassword
            )
        } catch (e: SSLHandshakeException) {
            Timber.e("Register with email SSLHandshakeException:")
            e.printStackTrace()
            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.SSLHandshakeException))
        } catch (e: CertPathValidatorException) {
            Timber.e("Register with email CertPathValidatorException:")
            e.printStackTrace()
            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.CertPathValidatorException))
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Register with email FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.FirebaseAuthUserCollisionException))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Register with email FirebaseNetworkException:")
            e.printStackTrace()
            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Register with email FirebaseNetworkException:")
            e.printStackTrace()
            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Register with email FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> RegisterWithEmailResult(
                    result = Result.Error(RegisterWithEmailError.Network.Firebase403)
                )
                else -> RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.SomethingWentWrong))
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Register with email Exception:")
            e.printStackTrace()
            RegisterWithEmailResult(result = Result.Error(RegisterWithEmailError.Network.SomethingWentWrong))
        }
    }
}