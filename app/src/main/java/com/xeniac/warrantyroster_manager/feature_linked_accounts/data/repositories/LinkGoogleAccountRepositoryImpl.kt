package com.xeniac.warrantyroster_manager.feature_linked_accounts.data.repositories

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.xeniac.warrantyroster_manager.core.data.utils.FirebaseErrorsHelper.isFirebase403Error
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.errors.LinkGoogleAccountError
import com.xeniac.warrantyroster_manager.feature_linked_accounts.domain.repositories.LinkGoogleAccountRepository
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class LinkGoogleAccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialManager: Lazy<CredentialManager>,
    private val googleIdOption: Lazy<GetGoogleIdOption>,
    private val firebaseAuth: Lazy<FirebaseAuth>
) : LinkGoogleAccountRepository {

    override suspend fun getGoogleCredential(): Result<Credential, GetGoogleCredentialError> {
        return try {
            val getCredentialRequest = GetCredentialRequest.Builder().apply {
                addCredentialOption(credentialOption = googleIdOption.get())
            }.build()

            val getCredentialResponse = credentialManager.get().getCredential(
                context = context,
                request = getCredentialRequest
            )

            Result.Success(getCredentialResponse.credential)
        } catch (e: GetCredentialCancellationException) {
            Timber.e("Get Google credential GetCredentialCancellationException:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.CancellationException)
        } catch (e: GetCredentialCustomException) {
            Timber.e("Get Google credential GetCredentialCustomException:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.CredentialCorruptedOrExpired)
        } catch (e: GetCredentialException) {
            Timber.e("Get Google credential GetCredentialException:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.AccessCredentialManagerFailed)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Get Google credential Exception:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.SomethingWentWrong)
        }
    }

    override suspend fun linkGoogleAccount(
        credential: Credential
    ): Result<Unit, LinkGoogleAccountError> {
        return try {
            when (credential) {
                is CustomCredential -> { // GoogleIdToken credential
                    when (credential.type) {
                        GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                            val googleCredential = GoogleIdTokenCredential.createFrom(
                                data = credential.data
                            )
                            val authCredential = GoogleAuthProvider.getCredential(
                                googleCredential.idToken,
                                null
                            )

                            firebaseAuth.get().currentUser?.let { currentUser ->
                                currentUser.linkWithCredential(authCredential).await()
                                return Result.Success(Unit)
                            }

                            Result.Error(LinkGoogleAccountError.Network.SomethingWentWrong)
                        }
                        else -> Result.Error(LinkGoogleAccountError.Network.UnexpectedCredentialType)
                    }
                }
                is PublicKeyCredential -> { // Passkey credential
                    Result.Error(LinkGoogleAccountError.Network.UnexpectedCredentialType)
                }
                is PasswordCredential -> { // Password credential
                    Result.Error(LinkGoogleAccountError.Network.UnexpectedCredentialType)
                }
                else -> Result.Error(LinkGoogleAccountError.Network.UnexpectedCredentialType)
            }
        } catch (e: GoogleIdTokenParsingException) {
            Timber.e("Link Google account GoogleIdTokenParsingException:")
            Result.Error(LinkGoogleAccountError.Network.GoogleIdTokenParsingException)
        } catch (e: SSLHandshakeException) {
            Timber.e("Link Google account SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Link Google account CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.CertPathValidatorException)
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Link Google account FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.FirebaseAuthUnauthorizedUser)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Link Google account FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.FirebaseAuthInvalidCredentialsException)
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Link Google account FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.FirebaseAuthUserCollisionException)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Link Google account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Link Google account FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Link Google account FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(LinkGoogleAccountError.Network.Firebase403)
                else -> Result.Error(LinkGoogleAccountError.Network.SomethingWentWrong)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Link Google account Exception:")
            e.printStackTrace()
            Result.Error(LinkGoogleAccountError.Network.SomethingWentWrong)
        }
    }
}