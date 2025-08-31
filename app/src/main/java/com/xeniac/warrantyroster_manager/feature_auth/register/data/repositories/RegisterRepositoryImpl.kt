package com.xeniac.warrantyroster_manager.feature_auth.register.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.SendVerificationEmailError
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.repositories.RegisterRepository
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

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : RegisterRepository {

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit, RegisterWithEmailError> {
        return try {
            withContext(context = Dispatchers.IO) {
                val result = firebaseAuth.get().createUserWithEmailAndPassword(
                    email, password
                ).await()

                val isSuccess = result.user != null
                if (isSuccess) {
                    return@withContext Result.Success(Unit)
                }

                Result.Error(RegisterWithEmailError.Network.SomethingWentWrong)
            }
        } catch (e: SSLHandshakeException) {
            Timber.e("Register with email SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(RegisterWithEmailError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Register with email CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(RegisterWithEmailError.Network.CertPathValidatorException)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Register with email Exception:")
            e.printStackTrace()
            Result.Error(RegisterWithEmailError.Network.SomethingWentWrong)
        }
    }

    override suspend fun sendVerificationEmail(
        user: FirebaseUser
    ): Result<Unit, SendVerificationEmailError> {
        return try {
            withContext(context = Dispatchers.IO) {
                firebaseAuth.get().currentUser?.sendEmailVerification()?.await()
                Result.Success(Unit)
            }
        } catch (e: SSLHandshakeException) {
            Timber.e("Send verification email SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Send verification email CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.CertPathValidatorException)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Send verification email Exception:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.SomethingWentWrong)
        }
    }
}