package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.firebase.FirebaseAuthInstance

class UserRepository {

    fun registerViaEmail(email: String, password: String) =
        FirebaseAuthInstance.auth.createUserWithEmailAndPassword(email, password)

    fun loginViaEmail(email: String, password: String) =
        FirebaseAuthInstance.auth.signInWithEmailAndPassword(email, password)

    fun sendResetPasswordEmail(email: String) =
        FirebaseAuthInstance.auth.sendPasswordResetEmail(email)

    fun getCurrentUser() = FirebaseAuthInstance.auth.currentUser

    fun sendVerificationEmail() = getCurrentUser()!!.sendEmailVerification()

    fun reloadCurrentUser(user: FirebaseUser) = user.reload()

    fun logoutUser() = FirebaseAuthInstance.auth.signOut()

    fun reAuthenticateUser(password: String) = getCurrentUser()!!.let {
        val credential = EmailAuthProvider.getCredential(it.email.toString(), password)
        getCurrentUser()!!.reauthenticate(credential)
    }

    fun updateUserEmail(newEmail: String) = getCurrentUser()!!.updateEmail(newEmail)

    fun updateUserPassword(newPassword: String) = getCurrentUser()!!.updatePassword(newPassword)
}