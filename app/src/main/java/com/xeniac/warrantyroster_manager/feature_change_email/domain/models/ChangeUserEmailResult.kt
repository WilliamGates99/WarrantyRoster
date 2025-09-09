package com.xeniac.warrantyroster_manager.feature_change_email.domain.models

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError

data class ChangeUserEmailResult(
    val passwordError: ChangeUserEmailError? = null,
    val newEmailError: ChangeUserEmailError? = null,
    val result: Result<Unit, ChangeUserEmailError>? = null
)