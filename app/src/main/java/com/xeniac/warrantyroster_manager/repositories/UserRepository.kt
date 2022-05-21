package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun registerViaEmail(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun loginViaEmail(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun sendResetPasswordEmail(email: String) =
        firebaseAuth.sendPasswordResetEmail(email)

    fun getCurrentUser() = firebaseAuth.currentUser

    fun sendVerificationEmail() = getCurrentUser()!!.sendEmailVerification()

    fun reloadCurrentUser(user: FirebaseUser) = user.reload()

    fun logoutUser() = firebaseAuth.signOut()

    fun reAuthenticateUser(password: String) = getCurrentUser()!!.let {
        val credential = EmailAuthProvider.getCredential(it.email.toString(), password)
        getCurrentUser()!!.reauthenticate(credential)
    }

    fun updateUserEmail(newEmail: String) = getCurrentUser()!!.updateEmail(newEmail)

    fun updateUserPassword(newPassword: String) = getCurrentUser()!!.updatePassword(newPassword)
}