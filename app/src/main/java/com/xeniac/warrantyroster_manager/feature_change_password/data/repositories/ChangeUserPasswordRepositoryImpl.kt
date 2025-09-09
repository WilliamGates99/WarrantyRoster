package com.xeniac.warrantyroster_manager.feature_change_password.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError
import com.xeniac.warrantyroster_manager.feature_change_password.domain.models.ChangeUserPasswordResult
import com.xeniac.warrantyroster_manager.feature_change_password.domain.repositories.ChangeUserPasswordRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class ChangeUserPasswordRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : ChangeUserPasswordRepository {

    override suspend fun changeUserPassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): ChangeUserPasswordResult {
        return try {
            firebaseAuth.get().currentUser?.let { currentUser ->
                val authCredential = EmailAuthProvider.getCredential(
                    currentUser.email.orEmpty(),
                    currentPassword.trim()
                )
                currentUser.reauthenticate(authCredential).await()

                currentUser.updatePassword(newPassword.trim()).await()
            }

            ChangeUserPasswordResult(result = Result.Success(Unit))
        } catch (e: IllegalArgumentException) {
            Timber.e("Register with email IllegalArgumentException:")
            e.printStackTrace()
            ChangeUserPasswordResult(
                newPasswordError = ChangeUserPasswordError.BlankNewPassword,
                confirmNewPasswordError = ChangeUserPasswordError.BlankConfirmNewPassword
            )
        } catch (e: SSLHandshakeException) {
            Timber.e("Change user password SSLHandshakeException:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.SSLHandshakeException))
        } catch (e: CertPathValidatorException) {
            Timber.e("Change user password CertPathValidatorException:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.CertPathValidatorException))
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Change user password FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.FirebaseAuthUnauthorizedUser))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Change user password FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.FirebaseAuthInvalidCredentialsException))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Change user password FirebaseNetworkException:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Change user password FirebaseNetworkException:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Change user password FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> ChangeUserPasswordResult(
                    result = Result.Error(ChangeUserPasswordError.Network.Firebase403)
                )
                else -> ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.SomethingWentWrong))
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Change user password Exception:")
            e.printStackTrace()
            ChangeUserPasswordResult(result = Result.Error(ChangeUserPasswordError.Network.SomethingWentWrong))
        }
    }
}