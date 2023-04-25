package com.xeniac.warrantyroster_manager.core.domain

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.xeniac.warrantyroster_manager.core.domain.model.UserInfo

interface UserRepository {

    suspend fun registerWithEmail(email: String, password: String)

    suspend fun loginWithEmail(email: String, password: String)

    suspend fun loginWithGoogleAccount(account: GoogleSignInAccount)

    suspend fun loginWithTwitterAccount(credential: AuthCredential)

    suspend fun loginWithFacebookAccount(accessToken: AccessToken)

    suspend fun sendResetPasswordEmail(email: String)

    suspend fun reloadCurrentUser()

    fun getCachedUserInfo(): UserInfo

    suspend fun getReloadedUserInfo(): UserInfo

    fun getCurrentUserUid(): String

    fun getCurrentUserEmail(): String

    suspend fun sendVerificationEmail()

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