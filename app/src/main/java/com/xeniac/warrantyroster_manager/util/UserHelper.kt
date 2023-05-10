package com.xeniac.warrantyroster_manager.util

import androidx.core.util.PatternsCompat
import com.xeniac.warrantyroster_manager.util.Constants.PASSWORD_STRENGTH_MEDIOCRE
import com.xeniac.warrantyroster_manager.util.Constants.PASSWORD_STRENGTH_STRONG
import com.xeniac.warrantyroster_manager.util.Constants.PASSWORD_STRENGTH_WEAK

object UserHelper {

    fun isEmailValid(email: String): Boolean =
        PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()

    fun isRetypePasswordValid(password: String, retypePassword: String): Boolean =
        password == retypePassword

    fun passwordStrength(password: String): Byte {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=(.*[\\W])*).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return when {
            password.length < 6 -> PASSWORD_STRENGTH_WEAK
            password.length < 8 -> PASSWORD_STRENGTH_MEDIOCRE
            else -> if (passwordMatcher.matches(password)) PASSWORD_STRENGTH_STRONG else PASSWORD_STRENGTH_MEDIOCRE
        }
    }
}