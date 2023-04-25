package com.xeniac.warrantyroster_manager.core.data.repository

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.xeniac.warrantyroster_manager.core.data.mapper.toUserInfo
import com.xeniac.warrantyroster_manager.core.domain.UserRepository
import com.xeniac.warrantyroster_manager.core.domain.model.UserInfo
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_FACEBOOK
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_GOOGLE
import com.xeniac.warrantyroster_manager.util.Constants.FIREBASE_AUTH_PROVIDER_ID_TWITTER
import kotlinx.coroutines.tasks.await
import timber.log.Timber
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

    override suspend fun reloadCurrentUser() {
        firebaseAuth.currentUser!!.reload().await()
    }

    override fun getCachedUserInfo(): UserInfo = firebaseAuth.currentUser!!.toUserInfo()

    override suspend fun getReloadedUserInfo(): UserInfo {
        val cachedUserInfo = getCachedUserInfo()

        reloadCurrentUser()
        val currentUser = firebaseAuth.currentUser!!
        val reloadedUserInfo = currentUser.toUserInfo()

        val isUserInfoChanged = reloadedUserInfo != cachedUserInfo

        return if (isUserInfoChanged) {
            Timber.i("Reloaded user info is $reloadedUserInfo")
            reloadedUserInfo
        } else {
            Timber.i("Reloaded user info is not changed.")
            Timber.i("Cached user info is $cachedUserInfo")
            cachedUserInfo
        }
    }

    override fun getCurrentUserUid(): String = firebaseAuth.currentUser!!.uid

    override fun getCurrentUserEmail(): String = firebaseAuth.currentUser!!.email.toString()

    override suspend fun sendVerificationEmail() {
        firebaseAuth.currentUser!!.sendEmailVerification().await()
    }

    override fun logoutUser() = firebaseAuth.signOut()

    override suspend fun reAuthenticateUser(password: String) {
        firebaseAuth.currentUser?.let {
            val credential = EmailAuthProvider.getCredential(it.email.toString(), password)
            it.reauthenticate(credential).await()
        }
    }

    override suspend fun getCurrentUserProviderIds(): List<String> {
        val providerIds = mutableListOf<String>()

        firebaseAuth.currentUser!!.providerData.forEach { userInfo ->
            providerIds.add(userInfo.providerId)
        }

        return providerIds
    }

    override suspend fun linkGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.currentUser!!.linkWithCredential(credential).await()
    }

    override suspend fun unlinkGoogleAccount() {
        firebaseAuth.currentUser!!.unlink(FIREBASE_AUTH_PROVIDER_ID_GOOGLE).await()
    }

    override suspend fun linkTwitterAccount(credential: AuthCredential) {
        firebaseAuth.currentUser!!.linkWithCredential(credential).await()
    }

    override suspend fun unlinkTwitterAccount() {
        firebaseAuth.currentUser!!.unlink(FIREBASE_AUTH_PROVIDER_ID_TWITTER).await()
    }

    override suspend fun linkFacebookAccount(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.currentUser!!.linkWithCredential(credential).await()
    }

    override suspend fun unlinkFacebookAccount() {
        firebaseAuth.currentUser!!.unlink(FIREBASE_AUTH_PROVIDER_ID_FACEBOOK).await()
    }

    override suspend fun updateUserEmail(newEmail: String) {
        firebaseAuth.currentUser!!.updateEmail(newEmail).await()
    }

    override suspend fun updateUserPassword(newPassword: String) {
        firebaseAuth.currentUser!!.updatePassword(newPassword).await()
    }
}