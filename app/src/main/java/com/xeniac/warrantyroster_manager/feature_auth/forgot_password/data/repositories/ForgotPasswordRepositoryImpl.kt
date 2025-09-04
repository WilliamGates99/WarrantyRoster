package com.xeniac.warrantyroster_manager.feature_auth.forgot_password.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.errors.SendResetPasswordEmailError
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.models.SendResetPasswordEmailResult
import com.xeniac.warrantyroster_manager.feature_auth.forgot_password.domain.repositories.ForgotPasswordRepository
import dagger.Lazy
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class ForgotPasswordRepositoryImpl @Inject constructor(
    private val firebaseAuth: Lazy<FirebaseAuth>
) : ForgotPasswordRepository {

    override suspend fun sendResetPasswordEmail(
        email: String
    ): SendResetPasswordEmailResult {
        return try {
            with(firebaseAuth.get()) {
                useAppLanguage()
                sendPasswordResetEmail(email.trim()).await()
            }

            SendResetPasswordEmailResult(result = Result.Success(Unit))
        } catch (e: IllegalArgumentException) {
            Timber.e("Send reset password email IllegalArgumentException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(
                emailError = SendResetPasswordEmailError.BlankEmail
            )
        } catch (e: SSLHandshakeException) {
            Timber.e("Login with email SSLHandshakeException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.SSLHandshakeException))
        } catch (e: CertPathValidatorException) {
            Timber.e("Login with email CertPathValidatorException:")
            e.printStackTrace()
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.CertPathValidatorException))
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Send reset password email Exception:")
            e.printStackTrace()
            // TODO: ERROR HANDLING
            SendResetPasswordEmailResult(result = Result.Error(SendResetPasswordEmailError.Network.SomethingWentWrong))
        }
    }
}