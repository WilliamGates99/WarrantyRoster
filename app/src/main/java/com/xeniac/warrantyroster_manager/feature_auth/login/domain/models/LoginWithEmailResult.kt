package com.xeniac.warrantyroster_manager.feature_auth.login.domain.models

import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.login.domain.errors.LoginWithEmailError

data class LoginWithEmailResult(
    val emailError: LoginWithEmailError? = null,
    val passwordError: LoginWithEmailError? = null,
    val result: Result<Unit, LoginWithEmailError>? = null
)