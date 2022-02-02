package com.xeniac.warrantyroster_manager.repositories

import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.firebase.FirebaseAuthInstance

class UserRepository {

    fun registerViaEmail(email: String, password: String) =
        FirebaseAuthInstance.auth.createUserWithEmailAndPassword(email, password)

    fun loginViaEmail(email: String, password: String) =
        FirebaseAuthInstance.auth.signInWithEmailAndPassword(email, password)

    fun sendResetPasswordEmail(email: String) =
        FirebaseAuthInstance.auth.sendPasswordResetEmail(email)

    fun sendVerificationEmail(user: FirebaseUser) = user.sendEmailVerification()

    fun getAccountDetails() = FirebaseAuthInstance.auth.currentUser

    fun reloadCurrentUser(user: FirebaseUser) = user.reload()

    fun logoutUser() = FirebaseAuthInstance.auth.signOut()
}