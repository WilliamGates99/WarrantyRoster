package com.xeniac.warrantyroster_manager.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override suspend fun registerViaEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun loginViaEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
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

    override suspend fun updateUserEmail(newEmail: String) {
        getCurrentUser().updateEmail(newEmail).await()
    }

    override suspend fun updateUserPassword(newPassword: String) {
        getCurrentUser().updatePassword(newPassword).await()
    }
}