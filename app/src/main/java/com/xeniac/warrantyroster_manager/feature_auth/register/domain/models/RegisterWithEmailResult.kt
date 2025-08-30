package com.xeniac.warrantyroster_manager.feature_auth.register.domain.models

import com.google.firebase.auth.FirebaseUser
import com.xeniac.warrantyroster_manager.core.domain.models.Result
import com.xeniac.warrantyroster_manager.feature_auth.register.domain.errors.RegisterWithEmailError

data class RegisterWithEmailResult(
    val emailError: RegisterWithEmailError? = null,
    val passwordError: RegisterWithEmailError? = null,
    val confirmPasswordError: RegisterWithEmailError? = null,
    val result: Result<FirebaseUser, RegisterWithEmailError>? = null
)