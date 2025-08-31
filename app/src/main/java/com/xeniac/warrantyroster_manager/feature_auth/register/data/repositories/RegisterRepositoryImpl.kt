package com.xeniac.warrantyroster_manager.feature_auth.register.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError
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
                    email.trim(),
                    password.trim()
                ).await()

                result.user?.let { registeredUser ->
                    registeredUser.sendEmailVerification().await()
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
}