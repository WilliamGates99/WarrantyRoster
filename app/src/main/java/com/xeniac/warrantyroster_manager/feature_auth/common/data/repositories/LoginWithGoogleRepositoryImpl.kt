package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

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
import com.xeniac.warrantyroster_manager.core.domain.repositories.WarrantyRosterDataStoreRepository
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.cert.CertPathValidatorException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.coroutineContext

class LoginWithGoogleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialManager: Lazy<CredentialManager>,
    private val googleIdOption: Lazy<GetGoogleIdOption>,
    private val firebaseAuth: Lazy<FirebaseAuth>,
    private val warrantyRosterDataStoreRepository: Lazy<WarrantyRosterDataStoreRepository>
) : LoginWithGoogleRepository {

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
            Timber.e("Get google credential GetCredentialCancellationException:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.GetGoogleCredentialCancellationException)
        } catch (e: GetCredentialException) {
            Timber.e("Get google credential GetCredentialException:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.AccessCredentialManagerFailed)
        } catch (e: GetCredentialCustomException) {
            Timber.e("Get google credential GetCredentialCustomException:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.CredentialCorruptedOrExpired)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Get google credential Exception:")
            e.printStackTrace()
            Result.Error(GetGoogleCredentialError.Network.SomethingWentWrong)
        }
    }

    override suspend fun loginWithGoogle(
        credential: Credential
    ): Result<Unit, LoginWithGoogleError> {
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

                            val authResult = firebaseAuth.get().signInWithCredential(
                                authCredential
                            ).await()

                            val isSuccess = authResult.user != null
                            if (isSuccess) {
                                warrantyRosterDataStoreRepository.get().isUserLoggedIn(
                                    isLoggedIn = true
                                )
                                return Result.Success(Unit)
                            }

                            Result.Error(LoginWithGoogleError.Network.SomethingWentWrong)
                        }
                        else -> Result.Error(LoginWithGoogleError.Network.UnexpectedCredentialType)
                    }
                }
                is PublicKeyCredential -> { // Passkey credential
                    Result.Error(LoginWithGoogleError.Network.UnexpectedCredentialType)
                }
                is PasswordCredential -> { // Password credential
                    Result.Error(LoginWithGoogleError.Network.UnexpectedCredentialType)
                }
                else -> Result.Error(LoginWithGoogleError.Network.UnexpectedCredentialType)
            }
        } catch (e: GoogleIdTokenParsingException) {
            Timber.e("Login with google GoogleIdTokenParsingException:")
            Result.Error(LoginWithGoogleError.Network.GoogleIdTokenParsingException)
        } catch (e: SSLHandshakeException) {
            Timber.e("Login with google SSLHandshakeException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.SSLHandshakeException)
        } catch (e: CertPathValidatorException) {
            Timber.e("Login with google CertPathValidatorException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.CertPathValidatorException)
        } catch (e: FirebaseNetworkException) {
            Timber.e("Login with google FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.FirebaseNetworkException)
        } catch (e: FirebaseTooManyRequestsException) {
            Timber.e("Login with google FirebaseNetworkException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.FirebaseTooManyRequestsException)
        } catch (e: FirebaseException) {
            Timber.e("Login with google FirebaseException:")
            e.printStackTrace()
            when {
                isFirebase403Error(e.message) -> Result.Error(LoginWithGoogleError.Network.Firebase403)
                else -> Result.Error(LoginWithGoogleError.Network.SomethingWentWrong)
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Timber.e("Login with google FirebaseAuthInvalidUserException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.FirebaseAuthInvalidUserException)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Timber.e("Login with google FirebaseAuthInvalidCredentialsException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.FirebaseAuthInvalidCredentialsException)
        } catch (e: FirebaseAuthUserCollisionException) {
            Timber.e("Login with google FirebaseAuthUserCollisionException:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.FirebaseAuthUserCollisionException)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Login with google Exception:")
            e.printStackTrace()
            Result.Error(LoginWithGoogleError.Network.SomethingWentWrong)
        }
    }
}