package com.xeniac.warrantyroster_manager.core.presentation.common.ui.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen

@Serializable
data object RegisterScreen

@Serializable
data object ForgotPasswordGraph {

    @Serializable
    data object ForgotPwScreen

    @Serializable
    data object ResetPwInstructionScreen
}