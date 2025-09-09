package com.xeniac.warrantyroster_manager.feature_change_email.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError
import com.xeniac.warrantyroster_manager.feature_change_email.domain.models.ChangeUserEmailResult
import com.xeniac.warrantyroster_manager.feature_change_email.domain.repositories.ChangeUserEmailRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class ChangeUserEmailRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : ChangeUserEmailRepository {

    override suspend fun changeUserEmail(
        password: String,
        newEmail: String
    ): ChangeUserEmailResult {
        return try {
            val finalNewEmail = newEmail.lowercase().trim()

            firebaseAuth.get().currentUser?.let { currentUser ->
                val isNewEmailSameAsCurrentEmail = finalNewEmail.equals(
                    other = currentUser.email,
                    ignoreCase = true
                )
                if (isNewEmailSameAsCurrentEmail) {
                    return ChangeUserEmailResult(
                        result = Result.Error(ChangeUserEmailError.SameNewEmailAsCurrentEmail)
                    )
                }

                val authCredential = EmailAuthProvider.getCredential(
                    currentUser.email.orEmpty(),
                    password.trim()
                )
                currentUser.reauthenticate(authCredential).await()

                currentUser.verifyBeforeUpdateEmail(finalNewEmail).await()
            }

            ChangeUserEmailResult(result = Result.Success(Unit))
        } catch (e: SSLHandshakeException) {
            Timber.e("Change user email SSLHandshakeException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.SSLHandshakeException))
        } catch (e: CertPathValidatorException) {
            Timber.e("Change user email CertPathValidatorException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.CertPathValidatorException))
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Change user email FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.FirebaseAuthUnauthorizedUser))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Change user email FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.FirebaseAuthInvalidCredentialsException))
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Change user email FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.FirebaseAuthUserCollisionException))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Change user email FirebaseNetworkException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Change user email FirebaseNetworkException:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Change user email FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> ChangeUserEmailResult(
                    result = Result.Error(ChangeUserEmailError.Network.Firebase403)
                )
                else -> ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.SomethingWentWrong))
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Change user email Exception:")
            e.printStackTrace()
            ChangeUserEmailResult(result = Result.Error(ChangeUserEmailError.Network.SomethingWentWrong))
        }
    }
}