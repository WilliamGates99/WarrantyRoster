package com.xeniac.warrantyroster_manager.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface UserRepository {

    suspend fun registerViaEmail(email: String, password: String)

    suspend fun loginViaEmail(email: String, password: String)

    suspend fun authenticateGoogleAccountWithFirebase(account: GoogleSignInAccount)

    suspend fun sendResetPasswordEmail(email: String)

    fun getCurrentUser(): Any

    fun getCurrentUserUid(): String

    fun getCurrentUserEmail(): String

    suspend fun sendVerificationEmail()

    suspend fun reloadCurrentUser()

    fun logoutUser()

    suspend fun reAuthenticateUser(password: String)

    suspend fun updateUserEmail(newEmail: String)

    suspend fun updateUserPassword(newPassword: String)
}