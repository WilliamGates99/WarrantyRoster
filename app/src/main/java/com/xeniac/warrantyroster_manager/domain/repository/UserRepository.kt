package com.xeniac.warrantyroster_manager.domain.repository

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface UserRepository {

    suspend fun registerViaEmail(email: String, password: String)

    suspend fun loginViaEmail(email: String, password: String)

    suspend fun loginWithGoogleAccount(account: GoogleSignInAccount)

    suspend fun loginWithTwitterAccount(credential: AuthCredential)

    suspend fun sendResetPasswordEmail(email: String)

    fun getCurrentUser(): FirebaseUser

    fun getCurrentUserUid(): String

    fun getCurrentUserEmail(): String

    suspend fun sendVerificationEmail()

    suspend fun reloadCurrentUser()

    fun logoutUser()

    suspend fun reAuthenticateUser(password: String)

    suspend fun getCurrentUserProviderIds(): List<String>

    suspend fun linkGoogleAccount(account: GoogleSignInAccount)

    suspend fun unlinkGoogleAccount()

    suspend fun linkTwitterAccount(credential: AuthCredential)

    suspend fun unlinkTwitterAccount()

    suspend fun linkFacebookAccount(accessToken: AccessToken)

    suspend fun unlinkFacebookAccount()

    suspend fun updateUserEmail(newEmail: String)

    suspend fun updateUserPassword(newPassword: String)
}