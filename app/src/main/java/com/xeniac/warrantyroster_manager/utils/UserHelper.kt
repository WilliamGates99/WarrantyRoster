package com.xeniac.warrantyroster_manager.utils

import android.util.Patterns

object UserHelper {

    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isRetypePasswordValid(password: String, retypePassword: String): Boolean =
        password == retypePassword

    fun passwordStrength(password: String): Byte {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=(.*[\\W])*).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return when {
            password.length < 6 -> -1
            password.length < 8 -> 0
            else -> if (passwordMatcher.matches(password)) 1 else 0
        }
    }
}