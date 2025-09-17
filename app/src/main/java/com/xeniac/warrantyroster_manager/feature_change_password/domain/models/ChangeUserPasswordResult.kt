package com.xeniac.warrantyroster_manager.feature_change_password.domain.models

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_change_email.domain.errors.ChangeUserEmailError
import com.xeniac.warrantyroster_manager.feature_change_password.domain.errors.ChangeUserPasswordError

data class ChangeUserPasswordResult(
    val currentPasswordError:ChangeUserPasswordError? = null,
    val newPasswordError: ChangeUserPasswordError? = null,
    val confirmNewPasswordError: ChangeUserPasswordError? = null,
    val result: Result<Unit, ChangeUserPasswordError>? = null
)