package com.xeniac.warrantyroster_manager.util

sealed class Resource<T>(
    val data: T? = null,
    val message: UiText? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: UiText, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}