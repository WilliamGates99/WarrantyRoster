package com.xeniac.warrantyroster_manager.feature_settings.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_settings.domain.errors.SendVerificationEmailError
import com.xeniac.warrantyroster_manager.feature_settings.domain.repositories.AccountVerificationRepository
import dagger.Lazy
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class AccountVerificationRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : AccountVerificationRepository {

    override suspend fun sendVerificationEmail(): Result<Unit, SendVerificationEmailError> {
        return try {
            val currentUser = firebaseAuth.get().currentUser

            if (currentUser?.email == null) {
                return Result.Error(SendVerificationEmailError.BlankEmail)
            }

            currentUser.sendEmailVerification().await()
            Result.Success(Unit)
        } catch (e: SSLHandshakeException) {
            Timber.e("Send verification email SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Send verification email CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.CertPathValidatorException)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Send verification email FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Send verification email FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Send verification email FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Send verification email FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(SendVerificationEmailError.Network.Firebase403)
                else -> Result.Error(SendVerificationEmailError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Send verification email Exception:")
            e.printStackTrace()
            Result.Error(SendVerificationEmailError.Network.SomethingWentWrong)
        }
    }
}