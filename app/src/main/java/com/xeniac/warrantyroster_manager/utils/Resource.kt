package com.xeniac.warrantyroster_manager.utils

import com.xeniac.warrantyroster_manager.models.Status

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> success(data: T?) = Resource(Status.SUCCESS, data, null)

        fun <T> error(message: String, data: T? = null) = Resource(Status.ERROR, data, message)

        fun <T> loading(data: T? = null) = Resource(Status.LOADING, data, null)
    }
}