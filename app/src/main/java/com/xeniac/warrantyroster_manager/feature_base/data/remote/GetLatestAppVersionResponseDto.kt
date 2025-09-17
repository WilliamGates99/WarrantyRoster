package com.xeniac.warrantyroster_manager.feature_base.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestAppVersionDto(
    @SerialName("version_code")
    val versionCode: Int,
    @SerialName("version_name")
    val versionName: String
)