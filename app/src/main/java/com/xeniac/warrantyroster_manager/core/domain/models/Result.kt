package com.xeniac.warrantyroster_manager.core.domain.models

import com.xeniac.warrantyroster_manager.core.domain.errors.Error

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}