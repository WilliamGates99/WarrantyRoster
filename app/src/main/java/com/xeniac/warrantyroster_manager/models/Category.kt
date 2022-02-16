package com.xeniac.warrantyroster_manager.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String,
    val title: Map<String, String>,
    val icon: String
) : Parcelable