package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordStrength

class PasswordStrengthCalculator {
    operator fun invoke(
        password: String?
    ): PasswordStrength? {
        if (password.isNullOrBlank()) {
            return null
        }

        val isPasswordShort = password.length < Constants.MIN_PASSWORD_LENGTH
        if (isPasswordShort) {
            return PasswordStrength.SHORT
        }

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val hasStrongPasswordLength = password.length >= Constants.STRONG_PASSWORD_LENGTH

        val criteriaMet = listOf(
            hasUpperCase,
            hasLowerCase,
            hasDigit,
            hasSpecialChar
        ).count { it }

        return when (criteriaMet) {
            4 -> when {
                hasStrongPasswordLength -> PasswordStrength.STRONG
                else -> PasswordStrength.MEDIOCRE
            }
            3 -> PasswordStrength.MEDIOCRE
            else -> PasswordStrength.WEAK
        }
    }
}