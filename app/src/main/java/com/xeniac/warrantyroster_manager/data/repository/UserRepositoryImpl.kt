package com.xeniac.warrantyroster_manager.data.repository

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.utils.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override suspend fun registerWithEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun loginWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun loginWithGoogleAccount(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials).await()
    }

    override suspend fun loginWithTwitterAccount(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun loginWithFacebookAccount(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun sendResetPasswordEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun getCurrentUser(): FirebaseUser = firebaseAuth.currentUser!!

    override fun getCurrentUserUid(): String = getCurrentUser().uid

    override fun getCurrentUserEmail(): String = getCurrentUser().email.toString()

    override suspend fun sendVerificationEmail() {
        getCurrentUser().sendEmailVerification().await()
    }

    override suspend fun reloadCurrentUser() {
        getCurrentUser().reload().await()
    }

    override fun logoutUser() = firebaseAuth.signOut()

    override suspend fun reAuthenticateUser(password: String) {
        getCurrentUser().let {
            val credential = EmailAuthProvider.getCredential(it.email.toString(), password)
            it.reauthenticate(credential).await()
        }
    }

    override suspend fun getCurrentUserProviderIds(): List<String> {
        val providerIds = mutableListOf<String>()

        getCurrentUser().providerData.forEach { userInfo ->
            providerIds.add(userInfo.providerId)
        }

        return providerIds
    }

    override suspend fun linkGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        getCurrentUser().linkWithCredential(credential).await()
    }

    override suspend fun unlinkGoogleAccount() {
        getCurrentUser().unlink(FIREBASE_AUTH_PROVIDER_ID_GOOGLE).await()
    }

    override suspend fun linkTwitterAccount(credential: AuthCredential) {
        getCurrentUser().linkWithCredential(credential).await()
    }

    override suspend fun unlinkTwitterAccount() {
        getCurrentUser().unlink(FIREBASE_AUTH_PROVIDER_ID_TWITTER).await()
    }

    override suspend fun linkFacebookAccount(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        getCurrentUser().linkWithCredential(credential).await()
    }

    override suspend fun unlinkFacebookAccount() {
        getCurrentUser().unlink(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK).await()
    }

    override suspend fun updateUserEmail(newEmail: String) {
        getCurrentUser().updateEmail(newEmail).await()
    }

    override suspend fun updateUserPassword(newPassword: String) {
        getCurrentUser().updatePassword(newPassword).await()
    }
}