package com.xeniac.warrantyroster_manager.utils

sealed class Resource<T>(
    val status: Status,
    val data: T? = null,
    val message: UiText? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(Status.SUCCESS, data)
    class Error<T>(message: UiText, data: T? = null) : Resource<T>(Status.ERROR, data, message)
    class Loading<T> : Resource<T>(Status.LOADING)
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}