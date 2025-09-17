package com.xeniac.warrantyroster_manager.feature_base.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LatestAppUpdateInfo(
    val versionCode: Int,
    val versionName: String
) : Parcelable