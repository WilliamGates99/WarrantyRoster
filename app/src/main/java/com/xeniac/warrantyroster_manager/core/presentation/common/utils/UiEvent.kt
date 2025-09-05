package com.xeniac.warrantyroster_manager.core.presentation.common.utils

sealed class UiEvent : Event() {
    data object RestartActivity : UiEvent()

    data object ShowOfflineSnackbar : UiEvent()
    data object ShowOfflineLongSnackbar : UiEvent()

    data class ShowShortSnackbar(val message: UiText) : UiEvent()
    data class ShowLongSnackbar(val message: UiText) : UiEvent()
    data class ShowActionSnackbar(val message: UiText) : UiEvent()

    data class ShowShortToast(val message: UiText) : UiEvent()
    data class ShowLongToast(val message: UiText) : UiEvent()

    data class Navigate(val destination: Any) : UiEvent()
    data object NavigateUp : UiEvent()

    data object ForceLogoutUnauthorizedUser : UiEvent()

    data object ClearCoilCache : UiEvent()
    data object NavigateToAuthScreen : UiEvent()
}