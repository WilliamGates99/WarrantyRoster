package com.xeniac.warrantyroster_manager.feature_auth.common.data.repositories

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.GetGoogleCredentialError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.errors.LoginWithGoogleError
import com.xeniac.warrantyroster_manager.feature_auth.common.domain.repositories.LoginWithGoogleRepository
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class LoginWithGoogleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialManager: Lazy<CredentialManager>,
    private val googleIdOption: Lazy<GetGoogleIdOption>,
    private val firebaseAuth: Lazy<FirebaseAuth>
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
        } catch (e: GetCredentialException) {
            Timber.e("Get google credential GetCredentialException:")
            e.printStackTrace()
            // TODO: HANDLE EXCEPTION
            Result.Error(GetGoogleCredentialError.Network.SomethingWentWrong)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Get google credential Exception:")
            e.printStackTrace()
            // TODO: HANDLE EXCEPTION
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
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(
                                data = credential.data
                            )
                            val authCredential = GoogleAuthProvider.getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )
                            firebaseAuth.get().signInWithCredential(authCredential).await()
                            Result.Success(Unit)
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
            // TODO: HANDLE EXCEPTION
            Result.Error(LoginWithGoogleError.Network.SomethingWentWrong)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Login with google Exception:")
            e.printStackTrace()
            // TODO: HANDLE EXCEPTION
            Result.Error(LoginWithGoogleError.Network.SomethingWentWrong)
        }
    }

    /* TODO: IMPLEMENT LOGOUT IN BASE SCREEN
    private suspend fun logoutUser() {
        try {
            firebaseAuth.get().signOut()

            // Clear the current user credential state from all credential providers
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.get().clearCredentialState(request = clearRequest)
        }catch (e: ClearCredentialException){

        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Timber.e("Logout user Exception:")
            e.printStackTrace()
            Result.Error(LogoutError.Network.SomethingWentWrong)
        }
    }
    */
}