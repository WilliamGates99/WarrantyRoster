package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.data.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.models.SendResetPasswordEmailResult
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.ForgotPasswordRepository
import dagger.Lazy
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class ForgotPasswordRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : ForgotPasswordRepository {

    override suspend fun sendResetPasswordEmail(
        email: String
    ): SendResetPasswordEmailResult {
        return try {
            with(firebaseAuth.get()) {
                useAppLanguage()
                sendPasswordResetEmail(email.lowercase().trim()).await()
            }

            SendResetPasswordEmailResult(result = Result.Success(Unit))
        } catch (e: IllegalArgumentException) {
            Timber.e("Send reset password email IllegalArgumentException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(
                emailError = SendResetPasswordEmailError.BlankEmail
            )
        } catch (e: SSLHandshakeException) {
            Timber.e("Send reset password email SSLHandshakeException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.SSLHandshakeException))
        } catch (e: CertPathValidatorException) {
            Timber.e("Send reset password email CertPathValidatorException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.CertPathValidatorException))
        } catch (e: FirebaseNetworkException) {
            Timber.e("Send reset password email FirebaseNetworkException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.FirebaseNetworkException))
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Send reset password email FirebaseNetworkException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.FirebaseTooManyRequestsException))
        } catch (e: FirebaseException) {
            Timber.e("Send reset password email FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> SendResetPasswordEmailResult(
                    result = Result.Error(SendResetPasswordEmailError.Network.Firebase403)
                )
                else -> SendResetPasswordEmailResult(
                    result = Result.Error(
                        SendResetPasswordEmailError.Network.SomethingWentWrong
                    )
                )
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            Timber.e("Send reset password email Exception:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.SomethingWentWrong))
        }
    }
}