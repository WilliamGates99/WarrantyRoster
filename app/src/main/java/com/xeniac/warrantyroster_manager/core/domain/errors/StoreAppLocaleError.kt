package com.xeniac.warrantyroster_manager.core.domain.errors

sealed class StoreAppLocaleError : Error() {
    data object SomethingWentWrong : StoreAppLocaleError()
}