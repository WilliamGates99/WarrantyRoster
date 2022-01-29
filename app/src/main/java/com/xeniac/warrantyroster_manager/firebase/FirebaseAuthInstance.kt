package com.xeniac.warrantyroster_manager.firebase

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthInstance {

    val auth by lazy {
        FirebaseAuth.getInstance()
    }
}