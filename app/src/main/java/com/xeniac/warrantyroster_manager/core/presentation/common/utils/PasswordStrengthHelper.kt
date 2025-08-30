package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import com.xeniac.warrantyroster_manager.core.domain.utils.Constants
import com.xeniac.warrantyroster_manager.core.presentation.common.states.PasswordStrength

class PasswordStrengthHelper {
    operator fun invoke(
        password: String?
    ): PasswordStrength {
        if (password.isNullOrBlank()) {
            return PasswordStrength.BLANK
        }

        val isPasswordShort = password.length < Constants.MIN_PASSWORD_LENGTH
        if (isPasswordShort) {
            return PasswordStrength.SHORT
        }

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        val criteriaMet = listOf(
            hasUpperCase,
            hasLowerCase,
            hasDigit,
            hasSpecialChar
        ).count { it }

        return when (criteriaMet) {
            4 -> PasswordStrength.STRONG
            3 -> PasswordStrength.MEDIOCRE
            else -> PasswordStrength.WEAK
        }
    }
}